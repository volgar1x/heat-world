package org.heat.world.controllers.events;

import org.heat.world.players.Player;

public final class FreePlayerEvent {
    private final Player player;

    public FreePlayerEvent(Player player) {this.player = player;}

    public Player getPlayer() {
        return player;
    }
}
