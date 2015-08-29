package org.heat.world.roleplay.environment;

import com.ankamagames.dofus.datacenter.world.Area;
import com.ankamagames.dofus.datacenter.world.SubArea;
import com.ankamagames.dofus.datacenter.world.SuperArea;
import com.ankamagames.dofus.network.enums.DirectionsEnum;
import com.google.common.primitives.Ints;
import lombok.Value;
import org.heat.shared.IntPair;

@Value
public final class FullWorldPosition implements WorldPosition {
    final WorldPositioningSystem wps;
    final SuperArea superArea;
    final Area area;
    final SubArea subArea;
    final WorldMap map;
    final IntPair mapCoordinates;
    final WorldMapPoint mapPoint;
    final DirectionsEnum direction;

    @Override
    public boolean isResolved() {
        return true;
    }

    @Override
    public int getSuperAreaId() {
        return superArea.getId();
    }

    @Override
    public int getAreaId() {
        return area.getId();
    }

    @Override
    public int getSubAreaId() {
        return subArea.getId();
    }

    @Override
    public int getMapId() {
        return Ints.checkedCast(map.getId());
    }

    @Override
    public WorldPosition moveTo(int mapId, WorldMapPoint point, DirectionsEnum dir) {
        return wps.locate(mapId, point, dir);
    }
}
