package org.heat.world.roleplay.environment;

import com.ankamagames.dofus.network.enums.DirectionsEnum;
import org.rocket.Service;

public interface WorldPositioningSystem extends Service {
    WorldPosition locate(int mapId, WorldMapPoint cellId, DirectionsEnum dir);
    WorldPosition resolve(WorldPosition position);
}
