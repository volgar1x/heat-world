package org.heat.world.controllers.events.roleplay;

import org.heat.world.players.Player;
import org.heat.world.users.WorldUser;

public final class NewFriendEvent {
    private final Player currentPlayer;
    private final WorldUser newFriend;

    public NewFriendEvent(Player currentPlayer, WorldUser newFriend) {
        this.currentPlayer = currentPlayer;
        this.newFriend = newFriend;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public WorldUser getNewFriend() {
        return newFriend;
    }
}
