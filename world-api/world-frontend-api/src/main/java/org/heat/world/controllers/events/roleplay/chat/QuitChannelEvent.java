package org.heat.world.controllers.events.roleplay.chat;

import org.heat.world.chat.WorldChannel;

public final class QuitChannelEvent {
    private final WorldChannel channel;

    public QuitChannelEvent(WorldChannel channel) {
        this.channel = channel;
    }

    public WorldChannel getChannel() {
        return channel;
    }
}
