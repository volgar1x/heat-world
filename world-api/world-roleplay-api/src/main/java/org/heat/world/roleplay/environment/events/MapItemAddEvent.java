package org.heat.world.roleplay.environment.events;

import org.heat.world.items.WorldItem;
import org.heat.world.roleplay.environment.WorldMap;
import org.heat.world.roleplay.environment.WorldMapPoint;

public class MapItemAddEvent extends WorldMapItemEvent {
    public MapItemAddEvent(WorldMap map, WorldItem item, WorldMapPoint mapPoint) {
        super(map, item, mapPoint);
    }
}
