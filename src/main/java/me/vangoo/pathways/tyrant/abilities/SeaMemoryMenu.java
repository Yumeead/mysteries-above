package me.vangoo.pathways.tyrant.abilities;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.valueobjects.Waypoint;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Меню «Морська Пам'ять» (Tyrant, Посл. 7): тримає лише UI/ефекти. Мітки читаються й
 * мутуються через {@code context.waypoints()} (персиститься), назви вводяться через
 * {@code context.ui().promptChatInput}, а показ маршруту стартує {@link SeaMemory#beginGuidance}.
 */
public class SeaMemoryMenu {

    private static final int CREATE_SLOT = 49;

    private final SeaMemory ability;

    SeaMemoryMenu(SeaMemory ability) {
        this.ability = ability;
    }

    void open(IAbilityContext context, Player caster) {
        UUID id = caster.getUniqueId();
        List<Waypoint> points = context.waypoints().list(id);
        int max = context.waypoints().maxPerPlayer();

        Gui gui = Gui.gui()
                .title(Component.text("⚓ Морська Пам'ять", NamedTextColor.AQUA))
                .rows(6)
                .disableAllInteractions()
                .create();
        gui.getFiller().fill(new GuiItem(named(Material.CYAN_STAINED_GLASS_PANE, " ")));

        for (int i = 0; i < points.size() && i < CREATE_SLOT; i++) {
            Waypoint wp = points.get(i);
            int index = i;
            gui.setItem(i, new GuiItem(waypointIcon(wp, i + 1), event -> {
                if (event.isShiftClick()) {
                    context.waypoints().remove(id, index);
                    context.messaging().sendMessage(id, "§7Мітку §f" + wp.name() + " §7видалено.");
                    open(context, caster); // reopen (замінює поточний вид)
                } else if (event.isRightClick()) {
                    gui.close(caster);
                    context.messaging().sendMessage(id, "§bВведіть нову назву мітки в чат:");
                    context.ui().promptChatInput(id, name -> {
                        Player p = Bukkit.getPlayer(id);
                        if (p == null || name.isBlank()) {
                            if (p != null) open(context, p);
                            return;
                        }
                        context.waypoints().rename(id, index, name.trim());
                        context.messaging().sendMessage(id, "§bМітку перейменовано на §f" + name.trim());
                        open(context, p);
                    });
                } else {
                    gui.close(caster);
                    ability.beginGuidance(context, caster, wp);
                }
            }));
        }

        gui.setItem(CREATE_SLOT, new GuiItem(createIcon(points.size(), max), event -> {
            gui.close(caster);
            if (context.waypoints().list(id).size() >= max) {
                context.messaging().sendMessage(id, "§cДосягнуто ліміту " + max + " міток.");
                return;
            }
            context.messaging().sendMessage(id, "§bВведіть назву нової мітки в чат:");
            context.ui().promptChatInput(id, name -> {
                Player p = Bukkit.getPlayer(id);
                if (p == null) {
                    return;
                }
                Location loc = p.getLocation();
                String finalName = name.isBlank()
                        ? "Мітка " + (context.waypoints().list(id).size() + 1)
                        : name.trim();
                boolean added = context.waypoints().add(id, new Waypoint(
                        finalName, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ()));
                if (!added) {
                    context.messaging().sendMessage(id, "§cДосягнуто ліміту " + max + " міток.");
                } else {
                    context.effects().playCircleEffect(loc, 1.5, Particle.SPLASH, 10);
                    context.effects().playSound(loc, Sound.AMBIENT_UNDERWATER_ENTER, 0.6f, 1.4f);
                    context.messaging().sendMessage(id, "§bМітку §f" + finalName + " §bзбережено: §f"
                            + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
                }
                open(context, p);
            });
        }));

        gui.open(caster);
    }

    private ItemStack waypointIcon(Waypoint wp, int number) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Світ: " + ChatColor.WHITE + wp.world());
        lore.add(ChatColor.GRAY + "Координати: " + ChatColor.WHITE
                + (int) wp.x() + ", " + (int) wp.y() + ", " + (int) wp.z());
        lore.add(" ");
        lore.add(ChatColor.YELLOW + "ЛКМ " + ChatColor.GRAY + "— вести до мітки");
        lore.add(ChatColor.YELLOW + "ПКМ " + ChatColor.GRAY + "— перейменувати");
        lore.add(ChatColor.YELLOW + "Shift " + ChatColor.GRAY + "— видалити");
        return coloredTile(Material.PRISMARINE_SHARD, ChatColor.AQUA + "" + number + ". " + wp.name(), lore);
    }

    private ItemStack createIcon(int count, int max) {
        List<String> lore = new ArrayList<>();
        if (count >= max) {
            lore.add(ChatColor.RED + "Ліміт " + max + " міток вичерпано");
        } else {
            lore.add(ChatColor.GRAY + "Зберегти поточне місце " + ChatColor.WHITE + "(" + count + "/" + max + ")");
            lore.add(ChatColor.GRAY + "Назву введете в чат.");
        }
        return coloredTile(Material.HEART_OF_THE_SEA, ChatColor.GREEN + "Створити мітку", lore);
    }

    private ItemStack coloredTile(Material material, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(loreLines);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack named(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
