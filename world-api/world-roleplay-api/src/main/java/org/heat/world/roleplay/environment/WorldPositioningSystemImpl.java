package org.heat.world.roleplay.environment;

import com.ankamagames.dofus.datacenter.world.Area;
import com.ankamagames.dofus.datacenter.world.MapPosition;
import com.ankamagames.dofus.datacenter.world.SubArea;
import com.ankamagames.dofus.datacenter.world.SuperArea;
import com.ankamagames.dofus.network.enums.DirectionsEnum;
import com.google.common.collect.Maps;
import org.fungsi.concurrent.Future;
import org.heat.datacenter.Datacenter;
import org.heat.shared.IntPair;
import org.rocket.ServicePath;
import org.rocket.StartReason;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.Map;

@Singleton
public class WorldPositioningSystemImpl implements WorldPositioningSystem {
    private final Datacenter datacenter;
    private final WorldMapRepository maps;
    private final Map<Integer, MapPosition> positions;

    @Inject
    public WorldPositioningSystemImpl(Datacenter datacenter, WorldMapRepository maps) {
        this.datacenter = datacenter;
        this.maps = maps;
        this.positions = Maps.newHashMap();

    }

    @Override
    public ServicePath path() {
        return ServicePath.absolute("wps");
    }

    @Override
    public ServicePath dependsOn() {
        return ServicePath.absolute("datacenter");
    }

    @Override
    public void start(StartReason reason) {
        Future<Map<Integer, MapPosition>> future = datacenter.findAll(MapPosition.class);
        Map<Integer, MapPosition> positions = future.get(Duration.ofSeconds(5));
        for (MapPosition position : positions.values()) {
            this.positions.put(position.getId(), position);
        }
    }

    @Override
    public void stop() {
        this.positions.clear();
    }

    @Override
    public WorldPosition locate(int mapId, WorldMapPoint mapPoint, DirectionsEnum dir) {
        MapPosition pos = positions.get(mapId);
        if (pos == null) {
            throw new UnresolvableWorldPositionException();
        }

        WorldMap map = maps.find(mapId).get();
        SubArea subArea = datacenter.find(SubArea.class, map.getSubareaId()).get();
        Area area = datacenter.find(Area.class, subArea.getAreaId()).get();
        SuperArea superArea = datacenter.find(SuperArea.class, area.getSuperAreaId()).get();

        return new FullWorldPosition(this, superArea, area, subArea, map, IntPair.of(pos.getPosX(), pos.getPosY()), mapPoint, dir);
    }

    @Override
    public WorldPosition resolve(WorldPosition position) {
        if (position instanceof UnresolvedWorldPosition) {
            return locate(position.getMapId(), position.getMapPoint(), position.getDirection());
        }

        return position;
    }
}
