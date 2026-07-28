package me.vangoo.pathways.tyrant.abilities;

import me.vangoo.domain.PathwayBranding;
import me.vangoo.domain.abilities.core.AbilityResult;
import me.vangoo.domain.abilities.core.ActiveAbility;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.services.SequenceScaler;
import me.vangoo.domain.valueobjects.Sequence;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Sequence 6: Шепіт Вітру. Вітер приносить Благословенному звуки чужого дихання —
 * усі живі поблизу на короткий час проступають крізь стіни (світіння лише для кастера).
 */
public class Eavesdrop extends ActiveAbility {

    private static final int BASE_RANGE = 15;
    private static final int BASE_DURATION_SECONDS = 8;
    private static final int COOLDOWN = 20;
    private static final int SPIRITUALITY_COST = 30;

    @Override
    public String getName() {
        return "Шепіт Вітру";
    }

    @Override
    public String getDescription(Sequence sequence) {
        int range = scaleValue(BASE_RANGE, sequence, SequenceScaler.ScalingStrategy.MODERATE);
        int duration = scaleValue(BASE_DURATION_SECONDS, sequence, SequenceScaler.ScalingStrategy.MODERATE);
        return String.format(
                "§fВітер приносить звуки чужого дихання: усе живе в радіусі §b%d §fблоків " +
                        "проступає крізь стіни на §b%d §fсекунд — видно лише вам.", range, duration);
    }

    @Override
    public int getSpiritualityCost() {
        return SPIRITUALITY_COST;
    }

    @Override
    public int getCooldown(Sequence sequence) {
        return COOLDOWN;
    }

    @Override
    protected AbilityResult performExecution(IAbilityContext context) {
        Player player = context.getCasterPlayer();
        if (player == null || !player.isValid()) {
            return AbilityResult.failure("Гравець недоступний");
        }

        Sequence sequence = context.getCasterBeyonder().getSequence();
        int range = scaleValue(BASE_RANGE, sequence, SequenceScaler.ScalingStrategy.MODERATE);
        int durationTicks = scaleValue(BASE_DURATION_SECONDS, sequence,
                SequenceScaler.ScalingStrategy.MODERATE) * 20;

        UUID casterId = context.getCasterId();
        List<LivingEntity> nearby = context.targeting().getNearbyEntities(range);

        int revealed = 0;
        for (LivingEntity entity : nearby) {
            if (entity.getUniqueId().equals(casterId)) continue;
            context.glowing().setGlowing(entity.getUniqueId(), casterId,
                    PathwayBranding.textOf("Tyrant"), durationTicks);
            revealed++;
        }

        Location center = player.getLocation();
        context.effects().playWaveEffect(center, range * 0.5, Particle.SMALL_GUST, 20);
        context.effects().playCircleEffect(player.getEyeLocation(), 1.0, Particle.GUST, 20);
        context.effects().playSound(center, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 0.7f, 0.6f);

        context.messaging().sendMessageToActionBar(casterId,
                Component.text("Вітер шепоче: поруч " + revealed + " присутностей").color(NamedTextColor.AQUA));
        return AbilityResult.success();
    }
}
