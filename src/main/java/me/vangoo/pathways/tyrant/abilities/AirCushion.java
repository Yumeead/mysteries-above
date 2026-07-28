package me.vangoo.pathways.tyrant.abilities;

import me.vangoo.domain.PathwayBranding;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.valueobjects.Sequence;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

/**
 * Sequence 6: Повітряна подушка. Вітер сам підхоплює Благословенного Вітром — падіння
 * гасне об подушку стисненого повітря, а збити його з ніг ще важче.
 * <p>
 * Прогресуюча версія {@link Balance} (спільний identity {@code tyrant_stance}): успадковує
 * керування атрибутом KNOCKBACK_RESISTANCE, підсилює його й додає імунітет до шкоди від
 * падіння через підписку на {@link EntityDamageEvent} — окремий Bukkit-слухач і вузол у
 * ServiceContainer не потрібні.
 */
public class AirCushion extends Balance {

    private static final double RESISTANCE = 0.5;
    private static final double LOUD_FALL_BLOCKS = 5.0; // з якої висоти показувати посадку

    @Override
    protected double resistance() {
        return RESISTANCE;
    }

    @Override
    public String getName() {
        return "Повітряна подушка";
    }

    @Override
    public String getDescription(Sequence sequence) {
        return "§fВітер згущується під вами при падінні: §bжодної шкоди від падіння§f, " +
                "а збити вас із ніг стало ще важче §b(посилений опір відкиданню)§f.";
    }

    @Override
    public void onActivate(IAbilityContext context) {
        super.onActivate(context);

        UUID casterId = context.getCasterId();
        context.events().subscribeToTemporaryEvent(
                casterId,
                EntityDamageEvent.class,
                event -> event.getEntity().getUniqueId().equals(casterId)
                        && event.getCause() == EntityDamageEvent.DamageCause.FALL,
                event -> {
                    event.setCancelled(true);
                    playLanding(context, (Player) event.getEntity());
                },
                Integer.MAX_VALUE
        );
    }

    @Override
    public void onDeactivate(IAbilityContext context) {
        super.onDeactivate(context);
        context.events().unsubscribeAll(context.getCasterId());
    }

    /** Посадка на подушці вітру: кільце кольору шляху + порив вітру під ногами. */
    private void playLanding(IAbilityContext context, Player player) {
        Location feet = player.getLocation();
        if (player.getFallDistance() < LOUD_FALL_BLOCKS) {
            context.effects().spawnParticle(Particle.SMALL_GUST, feet, 2);
            return;
        }

        Color color = PathwayBranding.liquidOf(context.getCasterBeyonder().getPathway().getName());
        context.effects().playExplosionRingEffect(feet, 0.5, Particle.DUST,
                new Particle.DustOptions(color, 1.4f));
        context.effects().spawnParticle(Particle.GUST, feet, 3, 0.4, 0.1, 0.4);
        context.effects().playSound(feet, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 0.9f, 1.4f);
    }
}
