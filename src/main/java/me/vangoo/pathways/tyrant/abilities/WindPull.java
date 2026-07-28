package me.vangoo.pathways.tyrant.abilities;

import me.vangoo.domain.abilities.core.AbilityResult;
import me.vangoo.domain.abilities.core.ActiveAbility;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.services.SequenceScaler;
import me.vangoo.domain.valueobjects.Sequence;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Sequence 6: Поклик Вітру. Благословенний Вітром закручує повітря навколо долоні —
 * і кинуті поблизу предмети самі злітають до руки.
 */
public class WindPull extends ActiveAbility {

    private static final int BASE_RADIUS = 8;
    private static final double PULL_FACTOR = 0.35;
    private static final double MAX_PULL_SPEED = 1.5;
    private static final int COOLDOWN = 4;
    private static final int SPIRITUALITY_COST = 20;

    @Override
    public String getName() {
        return "Поклик Вітру";
    }

    @Override
    public String getDescription(Sequence sequence) {
        int radius = scaleValue(BASE_RADIUS, sequence, SequenceScaler.ScalingStrategy.MODERATE);
        return String.format(
                "§fЗакручує повітря навколо долоні — усі кинуті предмети в радіусі §b%d §fблоків " +
                        "злітають просто до руки.", radius);
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

        int radius = scaleValue(BASE_RADIUS, context.getCasterBeyonder().getSequence(),
                SequenceScaler.ScalingStrategy.MODERATE);
        Location hand = player.getEyeLocation().subtract(0, 0.4, 0);

        int pulled = 0;
        for (Entity entity : player.getWorld().getNearbyEntities(hand, radius, radius, radius)) {
            if (!(entity instanceof Item item) || !item.isValid()) continue;
            if (item.getLocation().distance(hand) > radius) continue;

            item.setPickupDelay(0);
            Vector pull = hand.toVector().subtract(item.getLocation().toVector()).multiply(PULL_FACTOR);
            if (pull.length() > MAX_PULL_SPEED) pull.normalize().multiply(MAX_PULL_SPEED);
            item.setVelocity(pull);
            context.effects().playBeamEffect(item.getLocation(), hand, Particle.SMALL_GUST, 0.2, 3);
            pulled++;
        }

        if (pulled == 0) {
            return AbilityResult.failure("Поблизу немає предметів");
        }

        context.effects().playVortexEffect(hand, 1.2, 0.8, Particle.SMALL_GUST, 10);
        context.effects().playSound(hand, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 0.9f, 1.5f);
        return AbilityResult.success();
    }
}
