package org.heat.world.players.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.heat.world.roleplay.environment.WorldMapPoint;

/**
 * Managed by romain on 05/05/2015.
 */
@Getter
@RequiredArgsConstructor
public final class PlayerTeleportEvent {
    private final int mapId;
    private final WorldMapPoint point;
}
