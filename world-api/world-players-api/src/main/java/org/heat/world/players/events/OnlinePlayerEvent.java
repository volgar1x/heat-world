package org.heat.world.players.events;

import org.heat.world.players.Player;

public final class OnlinePlayerEvent extends PlayerEvent {
    public OnlinePlayerEvent(Player player) {
        super(player);
    }
}
