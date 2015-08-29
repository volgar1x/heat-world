package org.heat.world.roleplay.environment.events;

import org.heat.world.roleplay.WorldActor;
import org.heat.world.roleplay.environment.WorldMap;

public abstract class WorldMapActorEvent extends WorldMapEvent {
    private final WorldActor actor;

    protected WorldMapActorEvent(WorldMap map, WorldActor actor) {
        super(map);
        this.actor = actor;
    }

    public WorldActor getActor() {
        return actor;
    }
}
