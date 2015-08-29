package org.heat.world.controllers.events;

import org.heat.world.players.Player;

public final class ChoosePlayerEvent {
    private final Player player;

    public ChoosePlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
