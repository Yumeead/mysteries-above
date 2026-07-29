package me.vangoo.pathways.error.abilities;

import me.vangoo.domain.PathwayBranding;
import me.vangoo.domain.abilities.core.AbilityResourceConsumer;
import me.vangoo.domain.abilities.core.AbilityResult;
import me.vangoo.domain.abilities.core.ActiveAbility;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.entities.Beyonder;
import me.vangoo.domain.organizations.Institution;
import me.vangoo.domain.organizations.PathwayAccess;
import me.vangoo.domain.valueobjects.DreamStealerTheft;
import me.vangoo.domain.valueobjects.Sequence;
import me.vangoo.pathways.common.abilities.RitualMagic;
import me.vangoo.pathways.common.abilities.RitualSession;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Посл. 5 (Крадій снів): «Личина» — Крадіжка Таємниць Небес.
 *
 * <p>Обряд на вівтарі зі свічок (умова та сама, що в {@link RitualMagic}): злодій вимовляє
 * заклинання, останні рядки якого — канонічні «Вкради Таємниці Небес» / «Швидко, як наказ,
 * будь гнаний», і на 10 хв стає для обраної церкви своїм. Три наслідки одного обряду:
 * церква рахує його членом ({@code ChurchService.membershipOf}), вкрадена відповідь на
 * молитву дає посилення, а разом із чужою вірою приходить і Ритуальна магія.
 *
 * <p>Що ширший домен церкви, то вища ймовірність провалу (вікі: «the higher the
 * intelligence of the corresponding True Deity...»); провал викриває злодія — блискавка,
 * печатка здібностей і повний відкат.
 */
public class TheftOfHeavenlySecrets extends ActiveAbility {

    private static final List<String> INCANTATION = List.of(
            "Я молю силу, що чує молитви цього храму,",
            "я молю прийняти мене за свого,",
            "Вкради Таємниці Небес,",
            "швидко, як наказ, будь гнаний!");

    /** Обряд один на власника; повторний каст замінює й скасовує попередній (правило сесій). */
    private final Map<UUID, RitualSession> sessions = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "Личина";
    }

    @Override
    public String getDescription(Sequence userSequence) {
        return "Крадіжка Таємниць Небес: на вівтарі зі свічок ви вимовляєте чуже заклинання "
                + "й на " + DreamStealerTheft.DISGUISE_DURATION_MILLIS / 60_000
                + " хв стаєте вірним обраної церкви.\n"
                + "§7Церква рахує вас своїм, вкрадена відповідь на молитву б'є сильніше "
                + "(×" + DreamStealerTheft.DISGUISE_DAMAGE_MULTIPLIER
                + "), а разом із чужою вірою приходить Ритуальна магія.\n"
                + "§7Потрібно щонайменше " + DreamStealerTheft.DISGUISE_CANDLES_REQUIRED
                + " запалених свічок поруч.\n"
                + "§cЧим більше шляхів під церквою, тим імовірніше, що личина злетить — "
                + "а викритого чекає блискавка й "
                + DreamStealerTheft.DISGUISE_EXPOSURE_SECONDS + " с без здібностей.";
    }

    @Override
    public int getSpiritualityCost() {
        return DreamStealerTheft.DISGUISE_COST;
    }

    @Override
    public int getCooldown(Sequence userSequence) {
        return DreamStealerTheft.DISGUISE_COOLDOWN_SECONDS;
    }

    @Override
    protected AbilityResult performExecution(IAbilityContext context) {
        Location altar = context.getCasterLocation();
        int candles = RitualMagic.countLitCandles(altar);
        if (candles < DreamStealerTheft.DISGUISE_CANDLES_REQUIRED) {
            return AbilityResult.failure("Вівтар не готовий: потрібно щонайменше "
                    + DreamStealerTheft.DISGUISE_CANDLES_REQUIRED + " запалених свічок (зараз: "
                    + candles + ")");
        }
        context.ui().openChoiceMenu("Личина: чиїм вірним стати",
                context.church().churches(),
                this::createChurchIcon,
                church -> startRite(context, altar, church));
        return AbilityResult.deferred();
    }

    private void startRite(IAbilityContext context, Location altar, Institution church) {
        UUID casterId = context.getCasterId();
        Beyonder caster = context.getCasterBeyonder();
        Player player = context.getCasterPlayer();
        if (player == null) {
            return;
        }
        player.closeInventory();
        if (!AbilityResourceConsumer.consumeResources(this, caster, context)) {
            context.messaging().sendMessage(casterId, ChatColor.RED + "Недостатньо духовності!");
            return;
        }
        context.events().publishAbilityUsedEvent(this, caster);

        Color churchColor = colorOf(church);
        context.effects().playScriptureAura(altar.clone().add(0, 1.2, 0), churchColor, 120);
        context.effects().playSound(altar, Sound.BLOCK_BEACON_ACTIVATE, 0.9f, 0.8f);

        RitualSession previous = sessions.remove(casterId);
        if (previous != null) {
            previous.cancel();
        }
        RitualSession session = new RitualSession(casterId, altar, INCANTATION,
                () -> {
                    sessions.remove(casterId);
                    finishRite(context, altar, church);
                },
                reason -> {
                    sessions.remove(casterId);
                    context.messaging().sendMessage(casterId, ChatColor.RED
                            + "✗ Заклинання обірвано (" + reason + ") — небеса нічого не віддали.");
                });
        BukkitTask task = context.scheduling().scheduleRepeating(session::tick, 0L, 1L);
        session.bindTask(task);
        sessions.put(casterId, session);
    }

    private void finishRite(IAbilityContext context, Location altar, Institution church) {
        if (Math.random() >= DreamStealerTheft.disguiseSuccessChance(church.accesses().size())) {
            expose(context, altar, church);
            return;
        }
        UUID casterId = context.getCasterId();
        long durationMillis = DreamStealerTheft.DISGUISE_DURATION_MILLIS;
        int durationTicks = (int) (durationMillis / 50L);

        context.church().disguiseAs(casterId, church.id(), durationMillis);
        // Вкрадена відповідь на молитву: чужий домен якийсь час працює на злодія.
        context.amplification().amplifyDamage(casterId,
                DreamStealerTheft.DISGUISE_DAMAGE_MULTIPLIER, (int) (durationMillis / 1000L));
        context.entity().applyPotionEffect(casterId, PotionEffectType.RESISTANCE, durationTicks, 0);

        RitualMagic ritualMagic = new RitualMagic();
        boolean ritualGranted = context.getCasterBeyonder().addOffPathwayAbility(ritualMagic);
        context.beyonder().updateBeyonder(casterId);

        Color churchColor = colorOf(church);
        context.effects().playScriptureAura(altar.clone().add(0, 1.2, 0), churchColor, 100);
        BukkitTask halo = context.effects().playPersistentHalo(casterId, churchColor);
        context.effects().playSound(altar, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.2f);
        context.effects().playSound(altar, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.6f, 1.4f);
        context.messaging().sendMessage(casterId, ChatColor.LIGHT_PURPLE + "✦ Личина тримається: "
                + church.displayName() + " бачить у вас свого"
                + (ritualGranted ? ", а з нею до вас прийшла Ритуальна магія." : "."));

        context.beyonder().updateSanityLoss(casterId, DreamStealerTheft.DISGUISE_CORRUPTION);
        context.scheduling().scheduleDelayed(() -> {
            halo.cancel();
            context.church().dropDisguise(casterId);
            if (ritualGranted) {
                context.beyonder().removeOffPathwayAbility(ritualMagic.getIdentity(), casterId);
            }
            context.messaging().sendMessage(casterId, ChatColor.GRAY
                    + "Личина спала — храм більше не бачить у вас свого.");
        }, durationTicks);
    }

    /** Провал обряду: чужий домен упізнає злодія й б'є по ньому власною карою. */
    private void expose(IAbilityContext context, Location altar, Institution church) {
        UUID casterId = context.getCasterId();
        int seconds = DreamStealerTheft.DISGUISE_EXPOSURE_SECONDS;

        context.cooldown().lockAbilities(casterId, seconds);
        context.entity().applyPotionEffect(casterId, PotionEffectType.BLINDNESS, seconds * 20, 0);
        context.entity().applyPotionEffect(casterId, PotionEffectType.WEAKNESS, seconds * 20, 1);
        context.beyonder().updateSanityLoss(casterId, DreamStealerTheft.DISGUISE_CORRUPTION);

        context.effects().playHolyLightning(altar);
        context.effects().playExplosionRingEffect(altar, 2.2, Particle.DUST,
                new Particle.DustOptions(colorOf(church), 1.4f));
        context.effects().playSound(altar, Sound.BLOCK_BELL_RESONATE, 1.0f, 0.6f);
        context.messaging().sendMessageToActionBar(casterId,
                Component.text(ChatColor.RED + "Личину зірвано"));
        context.messaging().sendMessage(casterId, ChatColor.RED
                + "✗ " + church.displayName() + " упізнала самозванця — небеса відповіли карою.");
    }

    /** Колір церкви — брендинг її головного шляху; без доступів лишається сірий фолбек. */
    private Color colorOf(Institution church) {
        return PathwayBranding.liquidOf(church.accesses().isEmpty()
                ? null : church.accesses().get(0).pathwayName());
    }

    private ItemStack createChurchIcon(Institution church) {
        ItemStack item = new ItemStack(Material.BELL);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + church.displayName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + church.lore());
        lore.add(ChatColor.DARK_GRAY + "Шляхи домену: " + church.accesses().stream()
                .map(PathwayAccess::pathwayName)
                .reduce((a, b) -> a + ", " + b).orElse("—"));
        lore.add(ChatColor.YELLOW + "Шанс личини: "
                + Math.round(DreamStealerTheft.disguiseSuccessChance(church.accesses().size()) * 100)
                + "%");
        lore.add("");
        lore.add(ChatColor.YELLOW + "▶ Натисніть, щоб почати обряд");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void cleanUp() {
        sessions.values().forEach(RitualSession::cancel);
        sessions.clear();
    }
}
