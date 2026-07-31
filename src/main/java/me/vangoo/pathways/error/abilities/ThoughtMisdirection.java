package me.vangoo.pathways.error.abilities;

import me.vangoo.domain.abilities.core.AbilityResult;
import me.vangoo.domain.abilities.core.ActiveAbility;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.valueobjects.Sequence;
import me.vangoo.domain.valueobjects.SwindlerInfluence;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Посл. 8: «Підміна думок» — конус хибної інформації перед Аферистом.
 *
 * <p>Створіння в конусі перестають бачити ціль у кастері й накидаються одне на
 * одного; гравці на мить плутають, що саме тримають у руках. Нуль шкоди —
 * платить лише хибний намір.
 */
public class ThoughtMisdirection extends ActiveAbility {

    /** Період підтримки хибної цілі (тіки) — моб інакше швидко перецілиться назад. */
    private static final long RETARGET_PERIOD_TICKS = 10L;

    @Override
    public String getName() {
        return "Підміна думок";
    }

    @Override
    public String getDescription(Sequence userSequence) {
        return "Конус на " + (int) SwindlerInfluence.MISDIRECTION_CONE_RANGE + " блоків перед вами: "
                + SwindlerInfluence.misdirectionDurationTicks(userSequence) / 20
                + " с створіння б'ються між собою замість вас, а гравці плутають предмети в руках.";
    }

    @Override
    public int getSpiritualityCost() {
        return SwindlerInfluence.MISDIRECTION_COST;
    }

    @Override
    public int getCooldown(Sequence userSequence) {
        return SwindlerInfluence.MISDIRECTION_COOLDOWN_SECONDS;
    }

    @Override
    protected AbilityResult performExecution(IAbilityContext context) {
        UUID casterId = context.getCasterId();
        Player caster = context.getCasterPlayer();
        Vector direction = caster.getEyeLocation().getDirection();

        List<LivingEntity> inCone = entitiesInCone(context, caster, direction);
        if (inCone.isEmpty()) {
            return AbilityResult.failure("✖ Перед вами нікого, чиї думки можна підмінити");
        }

        List<Mob> mobs = inCone.stream().filter(e -> e instanceof Mob).map(e -> (Mob) e).toList();
        Map<UUID, LivingEntity> falseTargets = new HashMap<>();
        for (Mob mob : mobs) {
            LivingEntity victimOfLie = nearestOther(mobs, mob);
            falseTargets.put(mob.getUniqueId(), victimOfLie);
            mob.setTarget(victimOfLie);
        }

        for (LivingEntity entity : inCone) {
            if (entity instanceof Player victim
                    && ThreadLocalRandom.current().nextDouble() < SwindlerInfluence.MISDIRECTION_SCRAMBLE_CHANCE) {
                scrambleHands(context, victim);
            }
        }

        int duration = SwindlerInfluence.misdirectionDurationTicks(context.getCasterBeyonder().getSequence());
        holdTheLie(context, casterId, mobs, falseTargets, duration);

        playEffects(context, caster, direction);
        context.messaging().sendMessageToActionBar(casterId,
                Component.text("🎭 Ви підмінили думки " + inCone.size() + " цілей").color(NamedTextColor.LIGHT_PURPLE));
        return AbilityResult.success();
    }

    /** Живі цілі в конусі перед кастером (сам кастер виключений). */
    private List<LivingEntity> entitiesInCone(IAbilityContext context, Player caster, Vector direction) {
        double halfAngleCos = Math.cos(Math.toRadians(SwindlerInfluence.MISDIRECTION_CONE_ANGLE_DEGREES / 2.0));
        List<LivingEntity> result = new ArrayList<>();
        for (LivingEntity entity : context.targeting().getNearbyEntities(SwindlerInfluence.MISDIRECTION_CONE_RANGE)) {
            if (entity == null || entity.getUniqueId().equals(caster.getUniqueId())) continue;
            Vector toEntity = entity.getLocation().toVector().subtract(caster.getEyeLocation().toVector());
            if (toEntity.lengthSquared() < 1.0E-6) continue;
            if (toEntity.normalize().dot(direction) >= halfAngleCos) result.add(entity);
        }
        return result;
    }

    /** Найближчий інший моб конуса — той, кого «підставили». */
    private LivingEntity nearestOther(List<Mob> mobs, Mob self) {
        return mobs.stream()
                .filter(m -> !m.getUniqueId().equals(self.getUniqueId()))
                .min((a, b) -> Double.compare(
                        a.getLocation().distanceSquared(self.getLocation()),
                        b.getLocation().distanceSquared(self.getLocation())))
                .map(m -> (LivingEntity) m)
                .orElse(null);
    }

    /**
     * Обмежений у часі таск: поки триває ефект, моб, що знову взяв кастера на приціл,
     * повертається до хибної цілі. Таск скасовує сам себе — сесія тут зайва.
     */
    private void holdTheLie(IAbilityContext context, UUID casterId, List<Mob> mobs,
                            Map<UUID, LivingEntity> falseTargets, int durationTicks) {
        if (mobs.isEmpty()) return;
        BukkitTask[] holder = new BukkitTask[1];
        long[] elapsed = {0L};
        holder[0] = context.scheduling().scheduleRepeating(() -> {
            elapsed[0] += RETARGET_PERIOD_TICKS;
            if (elapsed[0] > durationTicks) {
                if (holder[0] != null) holder[0].cancel();
                return;
            }
            for (Mob mob : mobs) {
                if (!mob.isValid()) continue;
                LivingEntity current = mob.getTarget();
                if (current == null || current.getUniqueId().equals(casterId)) {
                    mob.setTarget(falseTargets.get(mob.getUniqueId()));
                }
            }
        }, RETARGET_PERIOD_TICKS, RETARGET_PERIOD_TICKS);
    }

    /**
     * Плутанина в руках: інший активний слот + обмін основної й другої руки.
     * Предмети між слотами НЕ переміщуються — це була б поверхня для дюпу.
     */
    private void scrambleHands(IAbilityContext context, Player victim) {
        PlayerInventory inventory = victim.getInventory();
        int held = inventory.getHeldItemSlot();
        inventory.setHeldItemSlot((held + 1 + ThreadLocalRandom.current().nextInt(8)) % 9);

        ItemStack main = inventory.getItemInMainHand();
        inventory.setItemInMainHand(inventory.getItemInOffHand());
        inventory.setItemInOffHand(main);

        context.messaging().sendMessageToActionBar(victim.getUniqueId(),
                Component.text("🎭 Ваші думки сплутались…").color(NamedTextColor.DARK_PURPLE));
        context.effects().playSoundForPlayer(victim.getUniqueId(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 2.0f);
        context.effects().spawnParticleForPlayer(victim.getUniqueId(), Particle.WITCH,
                victim.getEyeLocation(), 20, 0.5, 0.5, 0.5);
    }

    private void playEffects(IAbilityContext context, Player caster, Vector direction) {
        context.effects().playConeEffect(
                caster.getEyeLocation(),
                direction,
                SwindlerInfluence.MISDIRECTION_CONE_ANGLE_DEGREES,
                SwindlerInfluence.MISDIRECTION_CONE_RANGE,
                Particle.ENCHANT,
                30
        );
        context.effects().playSound(caster.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 1.1f);
    }
}
