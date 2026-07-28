package me.vangoo.pathways.tyrant.abilities;

import me.vangoo.domain.PathwayBranding;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.abilities.core.PermanentPassiveAbility;
import me.vangoo.domain.valueobjects.Sequence;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

/**
 * Sequence 5: Володіння Безоднею. Під водою Океанський Співець не боїться жодного
 * Потойбічного нижче Напівбога: вода гасить удари по ньому й підсилює його власні.
 * <p>
 * Пасив сам підписується на {@link EntityDamageEvent} (патерн {@link AirCushion}).
 * Одна підписка покриває обидва боки: {@link EntityDamageByEntityEvent} — підклас,
 * тож той самий слухач ловить і вхідну шкоду, і власні удари кастера.
 */
public class AbyssalDominion extends PermanentPassiveAbility {

    private static final double INCOMING_MULTIPLIER = 0.65; // -35% вхідної шкоди
    private static final double MELEE_MULTIPLIER = 1.4;     // +40% шкоди в ближньому бою

    @Override
    public String getName() {
        return "Володіння Безоднею";
    }

    @Override
    public String getDescription(Sequence sequence) {
        return "§fПоки ви §bу воді§f, безодня — ваша: вхідна шкода §b-35%§f, " +
                "а ваші удари в ближньому бою §c+40%§f.";
    }

    @Override
    public void tick(IAbilityContext context) {
        // Пасив реактивний — уся логіка в підписці на шкоду.
    }

    @Override
    public void onActivate(IAbilityContext context) {
        UUID casterId = context.getCasterId();
        context.events().subscribeToTemporaryEvent(
                casterId,
                EntityDamageEvent.class,
                event -> !event.isCancelled() && involvesCasterInWater(event, casterId),
                event -> apply(context, event, casterId),
                Integer.MAX_VALUE
        );
    }

    @Override
    public void onDeactivate(IAbilityContext context) {
        context.events().unsubscribeAll(context.getCasterId());
    }

    private static boolean involvesCasterInWater(EntityDamageEvent event, UUID casterId) {
        return victim(event, casterId) != null || meleeDamager(event, casterId) != null;
    }

    /** Кастер як ціль удару — лише коли він сам у воді. */
    private static Player victim(EntityDamageEvent event, UUID casterId) {
        return event.getEntity() instanceof Player player
                && player.getUniqueId().equals(casterId)
                && player.isInWater() ? player : null;
    }

    /** Кастер як нападник — лише коли він сам у воді. */
    private static Player meleeDamager(EntityDamageEvent event, UUID casterId) {
        return event instanceof EntityDamageByEntityEvent byEntity
                && byEntity.getDamager() instanceof Player player
                && player.getUniqueId().equals(casterId)
                && player.isInWater() ? player : null;
    }

    private void apply(IAbilityContext context, EntityDamageEvent event, UUID casterId) {
        Player defender = victim(event, casterId);
        if (defender != null) {
            event.setDamage(event.getDamage() * INCOMING_MULTIPLIER);
            playAbyssalShield(context, defender.getLocation());
            return;
        }
        event.setDamage(event.getDamage() * MELEE_MULTIPLIER);
    }

    /** Вода гасить удар: кільце кольору шляху + бульбашки й глухий сплеск. */
    private void playAbyssalShield(IAbilityContext context, Location at) {
        Color color = PathwayBranding.liquidOf(context.getCasterBeyonder().getPathway().getName());
        context.effects().playExplosionRingEffect(at, 0.9, Particle.DUST,
                new Particle.DustOptions(color, 1.2f));
        context.effects().spawnParticle(Particle.BUBBLE, at.clone().add(0, 1, 0), 12, 0.4, 0.6, 0.4);
        context.effects().playSound(at, Sound.AMBIENT_UNDERWATER_EXIT, 0.7f, 0.7f);
    }
}
