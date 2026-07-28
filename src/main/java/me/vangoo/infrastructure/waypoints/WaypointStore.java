package me.vangoo.infrastructure.waypoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.vangoo.domain.valueobjects.Waypoint;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * waypoints.json: збережені морські мітки гравців (Морська Пам'ять, Tyrant). До 10 на
 * гравця, переживають рестарт сервера. Пишеться після кожної мутації (як контракти).
 */
public class WaypointStore {

    public static final int MAX_PER_PLAYER = 10;

    private static final Logger LOGGER = Logger.getLogger(WaypointStore.class.getName());
    private static final Type MODEL_TYPE = new TypeToken<Map<String, List<Waypoint>>>() {}.getType();

    private final java.io.File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, List<Waypoint>> byPlayer = new ConcurrentHashMap<>();

    public WaypointStore(String filePath) {
        this.file = new java.io.File(filePath);
        load();
    }

    private void load() {
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try (Reader reader = new InputStreamReader(
                Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)) {
            Map<String, List<Waypoint>> raw = gson.fromJson(reader, MODEL_TYPE);
            if (raw != null) {
                raw.forEach((id, list) -> byPlayer.put(UUID.fromString(id), new ArrayList<>(list)));
            }
        } catch (IOException | RuntimeException e) {
            LOGGER.warning("Failed to load waypoints: " + e.getMessage());
        }
    }

    private void save() {
        Map<String, List<Waypoint>> raw = new HashMap<>();
        byPlayer.forEach((id, list) -> raw.put(id.toString(), list));
        try (Writer writer = new OutputStreamWriter(
                Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            gson.toJson(raw, writer);
        } catch (IOException e) {
            LOGGER.warning("Failed to save waypoints: " + e.getMessage());
        }
    }

    public List<Waypoint> list(UUID player) {
        return List.copyOf(byPlayer.getOrDefault(player, List.of()));
    }

    /** @return false, якщо ліміт {@link #MAX_PER_PLAYER} вичерпано. */
    public boolean add(UUID player, Waypoint waypoint) {
        List<Waypoint> list = byPlayer.computeIfAbsent(player, k -> new ArrayList<>());
        if (list.size() >= MAX_PER_PLAYER) {
            return false;
        }
        list.add(waypoint);
        save();
        return true;
    }

    public void remove(UUID player, int index) {
        List<Waypoint> list = byPlayer.get(player);
        if (list != null && index >= 0 && index < list.size()) {
            list.remove(index);
            save();
        }
    }

    public void rename(UUID player, int index, String name) {
        List<Waypoint> list = byPlayer.get(player);
        if (list != null && index >= 0 && index < list.size()) {
            list.set(index, list.get(index).withName(name));
            save();
        }
    }
}
