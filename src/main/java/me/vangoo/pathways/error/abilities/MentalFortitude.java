package me.vangoo.pathways.error.abilities;

import me.vangoo.domain.PathwayBranding;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.abilities.core.PermanentPassiveAbility;
import me.vangoo.domain.valueobjects.PrometheusTheft;
import me.vangoo.domain.valueobjects.Sequence;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Посл. 6: риса «Mental Resistance» — розум Прометея не піддається чужому впливу.
 *
 * <p>Три вектори одним пасивом:
 * <ul>
 *   <li>вхідна втрата глузду з БУДЬ-ЯКОГО джерела вдвічі менша — через
 *       {@link me.vangoo.domain.abilities.core.Ability#getSanityLossMultiplier()},
 *       який {@code Beyonder.increaseSanityLoss} застосовує до всіх джерел одразу;</li>
 *   <li>ментальні здібності інших гравців б'ють саме глуздом і ванільними ефектами —
 *       обидва канали перекриті;</li>
 *   <li>нудота, сліпота й темрява спадають самі, щойно з'являються.</li>
 * </ul>
 *
 * <p>Плюс повільне самовідновлення: раз на
 * {@link PrometheusTheft#MENTAL_FORTITUDE_RECOVERY_SECONDS} с розум повертає 1 глузду.
 */
public class MentalFortitude extends PermanentPassiveAbility {

    /** Ефекти, які Прометей просто не тримає в голові. */
    private static final List<PotionEffectType> MENTAL_EFFECTS =
            List.of(PotionEffectType.NAUSEA, PotionEffectType.BLINDNESS, PotionEffectType.DARKNESS);

    private static final long CHECK_PERIOD_MS = 1000;

    // Інстанс здібності спільний для шляху — стан тримаємо по гравцях.
    private final Map<UUID, Long> lastCheck = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastRecovery = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "Стійкість розуму";
    }

    @Override
    public String getDescription(Sequence userSequence) {
        return "Ваш розум звик до чужих думок.\n"
                + "Будь-яка втрата глузду зменшена на "
                + (int) Math.round((1.0 - PrometheusTheft.MENTAL_FORTITUDE_SANITY_MULTIPLIER) * 100)
                + "%, а нудота, сліпота й темрява спадають самі.\n"
                + "Кожні " + PrometheusTheft.MENTAL_FORTITUDE_RECOVERY_SECONDS
                + " с ви повертаєте 1 глузду.";
    }

    @Override
    public double getSanityLossMultiplier() {
        return PrometheusTheft.MENTAL_FORTITUDE_SANITY_MULTIPLIER;
    }

    @Override
    public void tick(IAbilityContext context) {
        UUID casterId = context.getCasterId();
        long now = System.currentTimeMillis();

        Long last = lastCheck.get(casterId);
        if (last != null && now - last < CHECK_PERIOD_MS) return;
        lastCheck.put(casterId, now);

        Player player = context.getCasterPlayer();
        if (player == null || !player.isValid()) return;

        clearMentalEffects(context, player);
        recoverSanity(context, casterId, now);
    }

    @Override
    public void onDeactivate(IAbilityContext context) {
        UUID casterId = context.getCasterId();
        lastCheck.remove(casterId);
        lastRecovery.remove(casterId);
    }

    private void clearMentalEffects(IAbilityContext context, Player player) {
        boolean cleared = false;
        for (PotionEffectType type : MENTAL_EFFECTS) {
            if (player.hasPotionEffect(type)) {
                context.entity().removePotionEffect(player.getUniqueId(), type);
                cleared = true;
            }
        }
        if (cleared) {
            context.effects().playFadingAura(player.getLocation(), PathwayBranding.liquidOf("Error"), 20);
            context.effects().playSoundForPlayer(player.getUniqueId(),
                    Sound.BLOCK_BEACON_DEACTIVATE, 0.4f, 1.6f);
        }
    }

    private void recoverSanity(IAbilityContext context, UUID casterId, long now) {
        if (context.getCasterBeyonder().getSanityLossScale() <= 0) {
            lastRecovery.put(casterId, now);
            return;
        }
        Long last = lastRecovery.putIfAbsent(casterId, now);
        if (last == null || now - last < PrometheusTheft.MENTAL_FORTITUDE_RECOVERY_SECONDS * 1000L) return;

        lastRecovery.put(casterId, now);
        context.beyonder().updateSanityLoss(casterId, -1);
    }
}
