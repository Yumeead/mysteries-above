package me.vangoo.domain.abilities.context;

import me.vangoo.domain.valueobjects.Waypoint;

import java.util.List;
import java.util.UUID;

/** Доступ здібностей до збережених морських міток (Морська Пам'ять, Tyrant). */
public interface IWaypointContext {

    List<Waypoint> list(UUID player);

    /** @return false, якщо ліміт вичерпано. */
    boolean add(UUID player, Waypoint waypoint);

    void remove(UUID player, int index);

    void rename(UUID player, int index, String name);

    int maxPerPlayer();
}
