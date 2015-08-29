package org.heat.world.controllers.events;

import org.heat.world.players.Player;

public final class CreatePlayerEvent {
    private final Player player;

    public CreatePlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
