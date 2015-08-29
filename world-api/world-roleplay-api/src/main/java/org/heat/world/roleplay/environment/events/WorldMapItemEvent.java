package org.heat.world.roleplay.environment.events;

import org.heat.world.items.WorldItem;
import org.heat.world.roleplay.environment.WorldMap;
import org.heat.world.roleplay.environment.WorldMapPoint;

public abstract class WorldMapItemEvent extends WorldMapEvent {
    private final WorldItem item;
    private final WorldMapPoint mapPoint;

    protected WorldMapItemEvent(WorldMap map, WorldItem item, WorldMapPoint mapPoint) {
        super(map);
        this.item = item;
        this.mapPoint = mapPoint;
    }

    public WorldItem getItem() {
        return item;
    }

    public WorldMapPoint getMapPoint() {
        return mapPoint;
    }
}
