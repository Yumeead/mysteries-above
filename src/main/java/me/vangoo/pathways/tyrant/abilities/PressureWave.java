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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Sequence 6: Владар Вітру. Благословенний Вітром вивільняє круговий шквал — стиснуте
 * повітря розлітається на всі боки, відкидає й ранить усе довкола, а над кастером на мить
 * закручується вихор-«ураганна мантія».
 */
public class PressureWave extends ActiveAbility {

    private static final int BASE_DAMAGE = 6;
    private static final double RADIUS = 12.0;
    private static final double KNOCKBACK = 1.4;
    private static final int COOLDOWN = 14;
    private static final int SPIRITUALITY_COST = 55;

    @Override
    public String getName() {
        return "Владар Вітру";
    }

    @Override
    public String getDescription(Sequence sequence) {
        int damage = scaleValue(BASE_DAMAGE, sequence, SequenceScaler.ScalingStrategy.MODERATE);
        return String.format(
                "§fКруговий шквал стиснутого повітря розлітається на §b%d §fблоків: усе довкола " +
                        "§cвідкидає §fі завдає §c%d §fшкоди.", (int) RADIUS, damage);
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
        int damage = scaleValue(BASE_DAMAGE, sequence, SequenceScaler.ScalingStrategy.MODERATE);

        Location center = player.getLocation();
        // Спіраль-«ураганна мантія» закручується навколо тіла (на рівні очей, не біля ніг),
        // щоб не «летіла» нижче прицілу.
        Location shroud = center.clone().add(0, 1.0, 0);

        context.effects().playVortexEffect(shroud, 3.5, 1.2, Particle.SMALL_GUST, 16);
        context.effects().playSound(center, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1.4f, 0.9f);

        for (Entity entity : player.getWorld().getNearbyEntities(center, RADIUS, RADIUS, RADIUS)) {
            if (!(entity instanceof LivingEntity target) || target.equals(player)) continue;
            if (target.getLocation().distance(center) > RADIUS) continue;
            target.damage(damage, player);
            Vector push = target.getLocation().toVector().subtract(center.toVector()).setY(0);
            if (push.lengthSquared() < 1.0e-4) push = new Vector(0, 0, 1); // цілі точно в центрі — штовхаємо кудись
            push.normalize().multiply(KNOCKBACK).setY(0.45);
            target.setVelocity(push);
        }

        return AbilityResult.success();
    }
}
