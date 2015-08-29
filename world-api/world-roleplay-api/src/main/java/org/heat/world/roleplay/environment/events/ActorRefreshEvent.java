package org.heat.world.roleplay.environment.events;

import org.heat.world.roleplay.WorldActor;
import org.heat.world.roleplay.environment.WorldMap;

public class ActorRefreshEvent extends WorldMapActorEvent {
    public ActorRefreshEvent(WorldMap map, WorldActor actor) {
        super(map, actor);
    }
}
