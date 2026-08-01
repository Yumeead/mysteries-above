package me.vangoo.infrastructure.bettermodel;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Копіює вшиті .bbmodel-моделі у plugins/BetterModel/models.
 * Репозиторій — єдине джерело правди: файли на сервері перезаписуються при розбіжності
 * (той самий контракт, що й у {@link me.vangoo.infrastructure.mythic.MythicPackInstaller}).
 *
 * <p>Тут НЕМАЄ жодного імпорту BetterModel: клас має лишатись безпечним для завантаження
 * навіть якщо плагіна на сервері нема (він у softdepend). Робота з його API — у
 * {@link BetterModelBridge}, який чіпають лише після перевірки, що плагін увімкнений.
 */
public final class BetterModelInstaller {

    /** Імена моделей без розширення; файл у джарі — bettermodel/models/<name>.bbmodel. */
    public static final String[] MODELS = {
            "tyrant_siren_5",
            "sea_condor",
            "fool_ancient_wraith_5",
            "fool_thousand_faced_hunter_6",
    };

    private static final String EXTENSION = ".bbmodel";

    private final Plugin plugin;

    public BetterModelInstaller(Plugin plugin) {
        this.plugin = plugin;
    }

    /** @return true, якщо хоч один файл було створено/оновлено (потрібен bettermodel reload). */
    public boolean installOrUpdate() {
        Path modelsRoot = plugin.getDataFolder().toPath()
                .resolveSibling("BetterModel").resolve("models");
        int updated = 0;
        for (String model : MODELS) {
            String rel = model + EXTENSION;
            try (InputStream in = plugin.getResource("bettermodel/models/" + rel)) {
                if (in == null) {
                    plugin.getLogger().warning("BetterModel resource missing in jar: " + rel);
                    continue;
                }
                byte[] data = in.readAllBytes();
                Path target = modelsRoot.resolve(rel);
                if (!Files.exists(target) || !Arrays.equals(Files.readAllBytes(target), data)) {
                    Files.createDirectories(target.getParent());
                    Files.write(target, data);
                    updated++;
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to install BetterModel file " + rel + ": " + e);
            }
        }
        if (updated > 0) {
            plugin.getLogger().info("BetterModel: installed/updated " + updated + " model(s)");
        }
        return updated > 0;
    }
}
