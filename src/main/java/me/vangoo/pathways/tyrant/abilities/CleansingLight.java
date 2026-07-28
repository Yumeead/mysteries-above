package me.vangoo.pathways.tyrant.abilities;

import me.vangoo.domain.abilities.core.AbilityResult;
import me.vangoo.domain.abilities.core.ActiveAbility;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.valueobjects.Sequence;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Sequence 7: Очищувальне Світло. Дрібна побутова водна магія Мореплавця: збиває полум'я
 * із себе й тих, хто горить поруч, і гасить вогонь довкола. Навмисно скромна — вірна лору
 * «очищення поверхонь», а не бойова.
 */
public class CleansingLight extends ActiveAbility {

    private static final double RADIUS = 4.0;
    private static final int COOLDOWN = 8;
    private static final int SPIRITUALITY_COST = 15;

    @Override
    public String getName() {
        return "Очищувальне Світло";
    }

    @Override
    public String getDescription(Sequence sequence) {
        return "§fЧиста вода збиває полум'я з вас і всіх, хто горить поруч, і гасить " +
                "вогонь у радіусі. Побутова магія — не для бою.";
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

        // Збити вогонь із себе й тих, хто горить поруч.
        player.setFireTicks(0);
        for (Entity entity : player.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
            if (entity.getFireTicks() > 0) entity.setFireTicks(0);
        }

        // Загасити блоки вогню в радіусі.
        Location center = player.getLocation();
        int r = (int) RADIUS;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    Block block = center.clone().add(x, y, z).getBlock();
                    if (block.getType() == Material.FIRE) block.setType(Material.AIR);
                }
            }
        }

        context.effects().playWaveEffect(center, RADIUS, Particle.FALLING_WATER, 12);
        context.effects().playCircleEffect(center.clone().add(0, 1, 0), 1.5, Particle.SPLASH, 12);
        context.effects().playSound(center, Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.2f);

        return AbilityResult.success();
    }
}
