package me.vangoo.application.services.context;

import me.vangoo.MysteriesAbovePlugin;
import me.vangoo.domain.abilities.context.IUIContext;
import me.vangoo.infrastructure.ui.ChoiceMenuFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class UIContext implements IUIContext {

    private final Player caster;
    private final MysteriesAbovePlugin plugin;

    public UIContext(Player caster, MysteriesAbovePlugin plugin) {
        this.caster = caster;
        this.plugin = plugin;
    }

    @Override
    public <T> void openChoiceMenu(String title, List<T> choices, Function<T, ItemStack> itemMapper, Consumer<T> onSelect) {
        ChoiceMenuFactory.openChoiceMenu(caster, title, choices, itemMapper, onSelect);
    }

    @Override
    public void monitorSneaking(UUID targetId, int durationTicks, Consumer<Boolean> callback) {
        new BukkitRunnable() {
            int currentTick = 0;

            @Override
            public void run() {
                Player player = Bukkit.getPlayer(targetId);

                if (player == null || !player.isOnline()) {
                    callback.accept(false);
                    this.cancel();
                    return;
                }

                if (player.isSneaking()) {
                    callback.accept(true);
                    this.cancel();
                    return;
                }

                currentTick += 5;
                if (currentTick >= durationTicks) {
                    callback.accept(false);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    @Override
    public void promptChatInput(UUID targetId, Consumer<String> callback) {
        AtomicBoolean done = new AtomicBoolean(false);
        Listener listener = new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onChat(AsyncPlayerChatEvent event) {
                if (!event.getPlayer().getUniqueId().equals(targetId)) {
                    return;
                }
                if (!done.compareAndSet(false, true)) {
                    return;
                }
                event.setCancelled(true); // не транслюємо ввід у загальний чат
                String message = event.getMessage();
                HandlerList.unregisterAll(this);
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(message));
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (done.compareAndSet(false, true)) {
                HandlerList.unregisterAll(listener);
            }
        }, 30L * 20L);
    }
}
