package org.heat.world.roleplay.environment;

import org.heat.world.roleplay.WorldActionCanceledException;

public class WorldMovementCanceledException extends WorldActionCanceledException {
    private final WorldMapPoint point;

    public WorldMovementCanceledException(WorldMapPoint point) {
        this.point = point;
    }

    public WorldMapPoint getPoint() {
        return point;
    }
}
