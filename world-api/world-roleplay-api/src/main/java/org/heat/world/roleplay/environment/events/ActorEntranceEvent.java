package org.heat.world.roleplay.environment.events;

import org.heat.world.roleplay.WorldActor;
import org.heat.world.roleplay.environment.WorldMap;

public class ActorEntranceEvent extends WorldMapActorEvent {
    private final boolean entering;

    public ActorEntranceEvent(WorldMap map, WorldActor actor, boolean entering) {
        super(map, actor);
        this.entering = entering;
    }

    public boolean isEntering() {
        return entering;
    }
}
