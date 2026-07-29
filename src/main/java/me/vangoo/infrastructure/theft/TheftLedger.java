package me.vangoo.infrastructure.theft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.vangoo.domain.valueobjects.AbilityIdentity;
import me.vangoo.domain.valueobjects.PrometheusTheft;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * theft.json: хто в кого вкрав здібність і коли (Крадіжка сили, Помилка Seq 6).
 *
 * <p>Час — абсолютна мітка `stolenAt` у мілісекундах епохи, ніколи не залишок тіків: лише так
 * вікно переживає релог і рестарт. Усе інше похідне — злодій тримає здібність до
 * {@code stolenAt + STOLEN_DURATION}, жертва не кастує її до {@code stolenAt + SUPPRESSION_DURATION}.
 *
 * <p>Строки живуть у самому записі (`expiresAt` / `suppressionEndsAt`), бо тіри різні: Посл. 6 —
 * 10/30 хв, Посл. 5 — 30/60 хв, а невикористана сила висить довше, доки її не покличуть уперше
 * ({@link #markUsed}). Записи, писані до цього поділу, мають нулі й читаються за сталими Прометея.
 *
 * <p>Один запис на злодія ({@link PrometheusTheft#MAX_CONCURRENT_STOLEN}); нова крадіжка заміщає стару.
 * Прострочене знімає періодичний {@link #sweep} — один прохід покриває і живе закінчення, і релог,
 * і рестарт, і крах.
 */
public class TheftLedger {

    private static final Logger LOGGER = Logger.getLogger(TheftLedger.class.getName());
    private static final Type MODEL_TYPE = new TypeToken<Map<String, Theft>>() {}.getType();

    /**
     * @param revoked          чи вже відібрано здібність у злодія (запис живе далі — до кінця пригнічення)
     * @param expiresAt        коли злодій втрачає силу; 0 у старих записах → строк Прометея
     * @param suppressionEndsAt коли жертва знову може кастувати; 0 у старих записах → строк Прометея
     * @param firstUsedAt      мітка першого використання; 0 — ще не використано
     * @param activeHoldMillis строк, на який перший каст перераховує володіння; 0 — резервного строку нема
     */
    public record Theft(String victim, String ability, long stolenAt, boolean revoked,
                        long expiresAt, long suppressionEndsAt, long firstUsedAt,
                        long activeHoldMillis) {

        /** Старі записи (Посл. 6) писались без явних меж — межі похідні від сталих Прометея. */
        public long expiresAt() {
            return expiresAt > 0 ? expiresAt : stolenAt + PrometheusTheft.STOLEN_DURATION_MILLIS;
        }

        public long suppressionEndsAt() {
            return suppressionEndsAt > 0
                    ? suppressionEndsAt
                    : stolenAt + PrometheusTheft.SUPPRESSION_DURATION_MILLIS;
        }

        public boolean used() {
            return firstUsedAt > 0;
        }

        Theft revoke() {
            return new Theft(victim, ability, stolenAt, true, expiresAt(), suppressionEndsAt(),
                    firstUsedAt, activeHoldMillis);
        }
    }

    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, Theft> byThief = new ConcurrentHashMap<>();

    public TheftLedger(String filePath) {
        this.file = new File(filePath);
        load();
    }

    private void load() {
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try (Reader reader = new InputStreamReader(
                Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)) {
            Map<String, Theft> raw = gson.fromJson(reader, MODEL_TYPE);
            if (raw != null) {
                raw.forEach((id, theft) -> byThief.put(UUID.fromString(id), theft));
            }
        } catch (IOException | RuntimeException e) {
            LOGGER.warning("Failed to load theft ledger: " + e.getMessage());
        }
    }

    private void save() {
        Map<String, Theft> raw = new HashMap<>();
        byThief.forEach((id, theft) -> raw.put(id.toString(), theft));
        try (Writer writer = new OutputStreamWriter(
                Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            gson.toJson(raw, writer);
        } catch (IOException e) {
            LOGGER.warning("Failed to save theft ledger: " + e.getMessage());
        }
    }

    /** Записати крадіжку зі строками Прометея (Посл. 6); попередній запис злодія заміщається. */
    public void add(UUID thief, UUID victim, AbilityIdentity ability) {
        add(thief, victim, ability,
                PrometheusTheft.STOLEN_DURATION_MILLIS, 0L,
                PrometheusTheft.SUPPRESSION_DURATION_MILLIS);
    }

    /**
     * Записати крадіжку з власними строками (Посл. 5 і концептуальні крадіжки).
     *
     * @param reserveHoldMillis скільки злодій тримає силу від цієї миті, доки не покличе її вперше
     * @param activeHoldMillis  на скільки перший каст перерахує строк; 0 — резервного строку нема
     * @param suppressionMillis скільки жертва її не кличе від цієї миті
     */
    public void add(UUID thief, UUID victim, AbilityIdentity ability,
                    long reserveHoldMillis, long activeHoldMillis, long suppressionMillis) {
        add(thief, victim, ability, reserveHoldMillis, activeHoldMillis, suppressionMillis,
                System.currentTimeMillis());
    }

    void add(UUID thief, UUID victim, AbilityIdentity ability, long reserveHoldMillis,
             long activeHoldMillis, long suppressionMillis, long now) {
        byThief.put(thief, new Theft(victim.toString(), ability.id(), now, false,
                now + reserveHoldMillis, now + suppressionMillis, 0L, activeHoldMillis));
        save();
    }

    /**
     * Позначити перше використання: невикористана сила висить довгим «резервним» строком
     * (вікі: тиждень), а з першим кастом строк перераховується на {@code activeHoldMillis}
     * самого запису. Строк володіння знає лише крадіжка, тож викликати можна на будь-який каст.
     *
     * @return {@code true}, якщо це справді було перше використання саме вкраденої сили
     */
    public boolean markUsed(UUID thief, AbilityIdentity ability) {
        return markUsed(thief, ability, System.currentTimeMillis());
    }

    boolean markUsed(UUID thief, AbilityIdentity ability, long now) {
        Theft theft = byThief.get(thief);
        if (theft == null || theft.revoked() || theft.used()
                || theft.activeHoldMillis() <= 0 || !theft.ability().equals(ability.id())) {
            return false;
        }
        byThief.put(thief, new Theft(theft.victim(), theft.ability(), theft.stolenAt(), false,
                now + theft.activeHoldMillis(), theft.suppressionEndsAt(), now,
                theft.activeHoldMillis()));
        save();
        return true;
    }

    /** Звільнити слот достроково: вкрадене витрачено (сон віддано, річ повернуто). */
    public void release(UUID thief) {
        if (byThief.remove(thief) != null) {
            save();
        }
    }

    /** Чи пригнічена в жертви ця здібність (вікно записане в самій крадіжці). */
    public boolean isSuppressed(UUID victim, AbilityIdentity ability) {
        return isSuppressed(victim, ability, System.currentTimeMillis());
    }

    boolean isSuppressed(UUID victim, AbilityIdentity ability, long now) {
        String victimId = victim.toString();
        return byThief.values().stream().anyMatch(t -> t.victim().equals(victimId)
                && t.ability().equals(ability.id())
                && now < t.suppressionEndsAt());
    }

    /** Що злодій тримає зараз (ще не відібране); порожньо — якщо нічого. */
    public java.util.Optional<Theft> grantOf(UUID thief) {
        return grantOf(thief, System.currentTimeMillis());
    }

    java.util.Optional<Theft> grantOf(UUID thief, long now) {
        Theft theft = byThief.get(thief);
        return theft != null && !theft.revoked() && now < theft.expiresAt()
                ? java.util.Optional.of(theft)
                : java.util.Optional.empty();
    }

    /**
     * Відібрати прострочені здібності й прибрати записи, у яких закінчилось і пригнічення.
     *
     * @param revoke кличеться рівно один раз на крадіжку: (злодій, здібність)
     */
    public void sweep(BiConsumer<UUID, AbilityIdentity> revoke) {
        sweep(revoke, System.currentTimeMillis());
    }

    void sweep(BiConsumer<UUID, AbilityIdentity> revoke, long now) {
        boolean changed = false;
        for (Map.Entry<UUID, Theft> entry : byThief.entrySet()) {
            Theft theft = entry.getValue();
            if (!theft.revoked() && now >= theft.expiresAt()) {
                revoke.accept(entry.getKey(), AbilityIdentity.of(theft.ability()));
                theft = theft.revoke();
                entry.setValue(theft);
                changed = true;
            }
            // Запис живе, доки не спав і строк злодія, і пригнічення жертви — вони незалежні.
            if (theft.revoked() && now >= theft.suppressionEndsAt()) {
                byThief.remove(entry.getKey());
                changed = true;
            }
        }
        if (changed) {
            save();
        }
    }
}
