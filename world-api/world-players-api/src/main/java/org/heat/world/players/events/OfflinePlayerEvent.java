package org.heat.world.players.events;

import org.heat.world.players.Player;

public final class OfflinePlayerEvent extends PlayerEvent {
    public OfflinePlayerEvent(Player player) {
        super(player);
    }
}
