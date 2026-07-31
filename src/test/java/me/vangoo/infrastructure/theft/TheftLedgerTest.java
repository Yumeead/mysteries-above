package me.vangoo.infrastructure.theft;

import me.vangoo.domain.valueobjects.AbilityIdentity;
import me.vangoo.domain.valueobjects.DreamStealerTheft;
import me.vangoo.domain.valueobjects.PrometheusTheft;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TheftLedgerTest {

    private static final AbilityIdentity ABILITY = AbilityIdentity.of("shadow_theft");

    @Test
    void stolenAbilityExpiresOnceAndSuppressionOutlivesIt(@TempDir Path dir) {
        UUID thief = UUID.randomUUID();
        UUID victim = UUID.randomUUID();
        TheftLedger ledger = new TheftLedger(dir.resolve("theft.json").toString());
        ledger.add(thief, victim, ABILITY);

        long now = System.currentTimeMillis();
        long stolen = PrometheusTheft.STOLEN_DURATION_MILLIS;
        long suppression = PrometheusTheft.SUPPRESSION_DURATION_MILLIS;
        List<UUID> revoked = new ArrayList<>();

        assertFalse(ledger.markUsed(thief, ABILITY, now), "у тіру Прометея резервного строку нема");

        // До 10 хв — здібність у злодія, жертва пригнічена.
        ledger.sweep((t, a) -> revoked.add(t), now + stolen / 2);
        assertTrue(revoked.isEmpty());
        assertTrue(ledger.grantOf(thief).isPresent());
        assertTrue(ledger.isSuppressed(victim, ABILITY, now + stolen / 2));

        // Після 10 хв — відібрано рівно один раз, пригнічення триває.
        ledger.sweep((t, a) -> revoked.add(t), now + stolen + 1);
        ledger.sweep((t, a) -> revoked.add(t), now + stolen + 2);
        assertEquals(List.of(thief), revoked);
        assertTrue(ledger.grantOf(thief).isEmpty());
        assertTrue(ledger.isSuppressed(victim, ABILITY, now + stolen + 1));

        // Після 30 хв — запис зникає, жертва вільна.
        ledger.sweep((t, a) -> revoked.add(t), now + suppression + 1);
        assertEquals(List.of(thief), revoked);
        assertFalse(ledger.isSuppressed(victim, ABILITY, now + suppression + 1));
    }

    @Test
    void ledgerSurvivesReload(@TempDir Path dir) {
        UUID thief = UUID.randomUUID();
        UUID victim = UUID.randomUUID();
        String path = dir.resolve("theft.json").toString();
        new TheftLedger(path).add(thief, victim, ABILITY);

        // Рестарт сервера: мітка абсолютна, тож вікна лічаться далі, а не з нуля.
        TheftLedger reloaded = new TheftLedger(path);
        assertTrue(reloaded.grantOf(thief).isPresent());
        assertTrue(reloaded.isSuppressed(victim, ABILITY));

        long past = System.currentTimeMillis() + PrometheusTheft.STOLEN_DURATION_MILLIS + 1;
        List<UUID> revoked = new ArrayList<>();
        reloaded.sweep((t, a) -> revoked.add(t), past);
        assertEquals(List.of(thief), revoked);

        // Знята здібність не відбирається вдруге навіть після ще одного рестарту.
        List<UUID> afterRestart = new ArrayList<>();
        new TheftLedger(path).sweep((t, a) -> afterRestart.add(t), past);
        assertTrue(afterRestart.isEmpty());
    }

    @Test
    void unusedPowerHangsUntilFirstUseThenRunsTheShortHold(@TempDir Path dir) {
        UUID thief = UUID.randomUUID();
        UUID victim = UUID.randomUUID();
        TheftLedger ledger = new TheftLedger(dir.resolve("theft.json").toString());

        long stolenAt = 1_000_000L;
        long hold = DreamStealerTheft.POWER_STOLEN_DURATION_MILLIS;
        long unusedHold = DreamStealerTheft.POWER_UNUSED_HOLD_MILLIS;
        long suppression = DreamStealerTheft.POWER_SUPPRESSION_DURATION_MILLIS;
        ledger.add(thief, victim, ABILITY, unusedHold, hold, suppression, stolenAt);

        // Не покликана — висить далеко за 30 хв володіння.
        assertTrue(ledger.grantOf(thief, stolenAt + hold + 1).isPresent());

        // Перший каст на 5-й годині: строк перераховується на 30 хв від цієї миті.
        long firstUse = stolenAt + 5L * 60 * 60_000L;
        assertFalse(ledger.markUsed(thief, AbilityIdentity.of("eloquence"), firstUse),
                "каст чужої, не вкраденої сили строк не запускає");
        assertTrue(ledger.markUsed(thief, ABILITY, firstUse));
        assertFalse(ledger.markUsed(thief, ABILITY, firstUse + 1), "друге використання строк не подовжує");
        assertTrue(ledger.grantOf(thief, firstUse + hold - 1).isPresent());
        assertTrue(ledger.grantOf(thief, firstUse + hold + 1).isEmpty());

        // Пригнічення жертви лічиться від крадіжки й першим кастом не рухається.
        assertTrue(ledger.isSuppressed(victim, ABILITY, stolenAt + suppression - 1));
        assertFalse(ledger.isSuppressed(victim, ABILITY, stolenAt + suppression + 1));
    }

    @Test
    void recordWrittenBeforePerTierDurationsFallsBackToPrometheus(@TempDir Path dir) throws Exception {
        UUID thief = UUID.randomUUID();
        UUID victim = UUID.randomUUID();
        Path file = dir.resolve("theft.json");
        long stolenAt = 2_000_000L;
        Files.writeString(file, """
                {"%s":{"victim":"%s","ability":"shadow_theft","stolenAt":%d,"revoked":false}}
                """.formatted(thief, victim, stolenAt));

        TheftLedger ledger = new TheftLedger(file.toString());
        TheftLedger.Theft theft = ledger.grantOf(thief, stolenAt).orElseThrow();
        assertEquals(stolenAt + PrometheusTheft.STOLEN_DURATION_MILLIS, theft.expiresAt());
        assertEquals(stolenAt + PrometheusTheft.SUPPRESSION_DURATION_MILLIS, theft.suppressionEndsAt());
        assertFalse(theft.used());
    }

    @Test
    void suppressionIsPerVictimAndAbility(@TempDir Path dir) {
        UUID thief = UUID.randomUUID();
        UUID victim = UUID.randomUUID();
        TheftLedger ledger = new TheftLedger(dir.resolve("theft.json").toString());
        ledger.add(thief, victim, ABILITY);

        assertFalse(ledger.isSuppressed(thief, ABILITY), "злодій кастує вкрадене вільно");
        assertFalse(ledger.isSuppressed(victim, AbilityIdentity.of("eloquence")));
    }
}
