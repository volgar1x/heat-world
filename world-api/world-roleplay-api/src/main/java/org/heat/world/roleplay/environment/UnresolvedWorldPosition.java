package org.heat.world.roleplay.environment;

import com.ankamagames.dofus.datacenter.world.Area;
import com.ankamagames.dofus.datacenter.world.SubArea;
import com.ankamagames.dofus.datacenter.world.SuperArea;
import com.ankamagames.dofus.network.enums.DirectionsEnum;
import lombok.Value;
import lombok.experimental.Wither;
import org.heat.shared.IntPair;

@Value
@Wither
public final class UnresolvedWorldPosition implements WorldPosition {
    final int mapId;
    final WorldMapPoint mapPoint;
    final DirectionsEnum direction;

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public SuperArea getSuperArea() {
        throw new UnresolvedWorldPositionException();
    }

    @Override
    public Area getArea() {
        throw new UnresolvedWorldPositionException();
    }

    @Override
    public SubArea getSubArea() {
        throw new UnresolvedWorldPositionException();
    }

    @Override
    public WorldMap getMap() {
        throw new UnresolvedWorldPositionException();
    }

    @Override
    public int getSuperAreaId() {
        throw new UnresolvedWorldPositionException();
    }

    @Override
    public int getAreaId() {
        throw new UnresolvedWorldPositionException();
    }

    @Override
    public int getSubAreaId() {
        throw new UnresolvedWorldPositionException();
    }

    @Override
    public IntPair getMapCoordinates() {
        throw new UnresolvedWorldPositionException();
    }

    @Override
    public WorldPosition moveTo(int mapId, WorldMapPoint point, DirectionsEnum dir) {
        return new UnresolvedWorldPosition(mapId, point, dir);
    }
}
