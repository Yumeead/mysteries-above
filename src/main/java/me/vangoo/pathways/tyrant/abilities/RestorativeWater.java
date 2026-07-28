package me.vangoo.pathways.tyrant.abilities;

import me.vangoo.domain.abilities.core.AbilityResult;
import me.vangoo.domain.abilities.core.ActiveAbility;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.services.SequenceScaler;
import me.vangoo.domain.valueobjects.Sequence;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

/**
 * Sequence 7: Цілюща Вода. Мореплавець огортає себе чи союзника, на якого дивиться,
 * живлющою водою — слабка регенерація. Навмисно слабша за зілля («поступається»),
 * але швидка й безкоштовна для матеріалів підтримка в бою.
 */
public class RestorativeWater extends ActiveAbility {

    private static final int BASE_DURATION = 80;   // ~4 с REGENERATION I
    private static final double MAX_RANGE = 20.0;
    private static final int COOLDOWN = 20;
    private static final int SPIRITUALITY_COST = 35;

    @Override
    public String getName() {
        return "Цілюща Вода";
    }

    @Override
    public String getDescription(Sequence sequence) {
        int seconds = scaleValue(BASE_DURATION, sequence, SequenceScaler.ScalingStrategy.MODERATE) / 20;
        return String.format(
                "§fЖивлюща вода огортає вас або союзника, на якого ви дивитесь: слабка " +
                        "§aрегенерація §fна §a%d §fс. Поступається зіллям, зате миттєва.", seconds);
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
        int duration = scaleValue(BASE_DURATION, sequence, SequenceScaler.ScalingStrategy.MODERATE);

        // Ціль — союзник на лінії зору, інакше сам кастер.
        Optional<LivingEntity> targeted = context.targeting().getTargetedEntity(MAX_RANGE);
        LivingEntity target = targeted.filter(e -> e instanceof Player).orElse(player);

        context.entity().applyPotionEffect(target.getUniqueId(), PotionEffectType.REGENERATION, duration, 0);

        context.effects().playSphereEffect(target.getEyeLocation(), 1.0, Particle.SPLASH, 20);
        context.effects().playCircleEffect(target.getLocation(), 1.2, Particle.FALLING_WATER, 20);
        context.effects().playSound(target.getLocation(), Sound.BLOCK_WATER_AMBIENT, 1.0f, 1.2f);

        return AbilityResult.success();
    }
}
