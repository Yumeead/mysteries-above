package me.vangoo.pathways.tyrant.abilities;

import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.abilities.core.PermanentPassiveAbility;
import me.vangoo.domain.valueobjects.Sequence;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Sequence 9: Ілюзорна луска. Прихована під шкірою луска робить Матроса слизьким
 * і твердим на дотик, мов риба. Механічно — опір шкоді (RESISTANCE), але лише поки
 * тіло у воді (де луска «оживає»). На суші ефекту немає.
 */
public class IllusoryScales extends PermanentPassiveAbility {

    private static final int CHECK_PERIOD = 10;   // перевіряти воду двічі на секунду
    private static final int EFFECT_TICKS = 30;   // трохи довше за період — плавне згасання
    private static final int AMPLIFIER = 0;       // RESISTANCE I на цьому Sequence

    private int tickCounter = 0;

    @Override
    public String getName() {
        return "Ілюзорна луска";
    }

    @Override
    public String getDescription(Sequence sequence) {
        return "§fПрихована під шкірою луска робить вас слизьким і твердим, мов риба. " +
                "У воді вона дає §bопір шкоді§f.";
    }

    @Override
    public void tick(IAbilityContext context) {
        UUID casterId = context.getCasterId();
        if (!context.playerData().isOnline(casterId)) return;

        tickCounter++;
        if (tickCounter % CHECK_PERIOD != 0) return;

        Player player = context.getCasterPlayer();
        if (player == null || !player.isValid()) return;

        if (player.isInWater()) {
            context.entity().applyPotionEffect(casterId, PotionEffectType.RESISTANCE, EFFECT_TICKS, AMPLIFIER);
        } else {
            context.entity().removePotionEffect(casterId, PotionEffectType.RESISTANCE);
        }
    }

    @Override
    public void onDeactivate(IAbilityContext context) {
        UUID casterId = context.getCasterId();
        if (context.playerData().isOnline(casterId)) {
            context.entity().removePotionEffect(casterId, PotionEffectType.RESISTANCE);
        }
    }
}
