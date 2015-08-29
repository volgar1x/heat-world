package org.heat.world.roleplay.environment.events;

import org.heat.world.roleplay.environment.WorldMap;

public abstract class WorldMapEvent {
    private final WorldMap map;

    protected WorldMapEvent(WorldMap map) {
        this.map = map;
    }

    public WorldMap getMap() {
        return map;
    }
}
