package me.vangoo.application.services.context;

import me.vangoo.domain.abilities.context.IWaypointContext;
import me.vangoo.domain.valueobjects.Waypoint;
import me.vangoo.infrastructure.waypoints.WaypointStore;

import java.util.List;
import java.util.UUID;

public class WaypointContext implements IWaypointContext {

    private final WaypointStore store;

    public WaypointContext(WaypointStore store) {
        this.store = store;
    }

    @Override
    public List<Waypoint> list(UUID player) {
        return store.list(player);
    }

    @Override
    public boolean add(UUID player, Waypoint waypoint) {
        return store.add(player, waypoint);
    }

    @Override
    public void remove(UUID player, int index) {
        store.remove(player, index);
    }

    @Override
    public void rename(UUID player, int index, String name) {
        store.rename(player, index, name);
    }

    @Override
    public int maxPerPlayer() {
        return WaypointStore.MAX_PER_PLAYER;
    }
}
