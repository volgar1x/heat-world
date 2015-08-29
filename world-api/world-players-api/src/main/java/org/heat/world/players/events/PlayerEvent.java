package org.heat.world.players.events;

import org.heat.world.players.Player;

public abstract class PlayerEvent {
    private final Player player;

    protected PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
