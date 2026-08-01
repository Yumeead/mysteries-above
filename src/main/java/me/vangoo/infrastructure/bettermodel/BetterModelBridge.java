package me.vangoo.infrastructure.bettermodel;

import kr.toxicity.model.api.BetterModel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Єдине місце, що торкається API BetterModel.
 *
 * <p>BetterModel у softdepend — на сервері його може не бути, тому класи
 * {@code kr.toxicity.model.*} не можна згадувати там, де код виконується безумовно:
 * JVM лінкує їх при завантаженні класу і без плагіна впаде з NoClassDefFoundError.
 * Звідси розділення: {@link BetterModelInstaller} (чистий file-I/O, завжди безпечний)
 * і цей клас, який кличуть ЛИШЕ після {@link #isAvailable()}.
 */
public final class BetterModelBridge {

    private static final String PLUGIN_NAME = "BetterModel";

    private BetterModelBridge() {}

    /** true, якщо плагін BetterModel присутній і увімкнений. Перевіряй ПЕРЕД будь-яким іншим методом. */
    public static boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME);
    }

    /**
     * Логує, чи справді зареєструвалась кожна модель із {@link BetterModelInstaller#MODELS}.
     * Без цієї перевірки зламана/незавантажена модель проявляється лише в грі — моб спавниться
     * звичайним ванільним мобом і жодної помилки в консолі нема.
     */
    public static void verifyModelsLoaded(Plugin plugin) {
        for (String model : BetterModelInstaller.MODELS) {
            if (BetterModel.model(model).isEmpty()) {
                plugin.getLogger().warning("BetterModel model '" + model
                        + "' is not loaded — mobs using it will render as plain vanilla entities."
                        + " Check plugins/BetterModel/models/ and run /bettermodel reload.");
            }
        }
    }
}
