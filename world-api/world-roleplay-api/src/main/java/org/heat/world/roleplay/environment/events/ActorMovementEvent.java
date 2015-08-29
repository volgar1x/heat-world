package org.heat.world.roleplay.environment.events;

import org.heat.world.roleplay.WorldActor;
import org.heat.world.roleplay.environment.WorldMap;
import org.heat.world.roleplay.environment.WorldMapPath;

public class ActorMovementEvent extends WorldMapActorEvent {
    private final WorldMapPath path;

    public ActorMovementEvent(WorldMap map, WorldActor actor, WorldMapPath path) {
        super(map, actor);
        this.path = path;
    }

    public WorldMapPath getPath() {
        return path;
    }
}
