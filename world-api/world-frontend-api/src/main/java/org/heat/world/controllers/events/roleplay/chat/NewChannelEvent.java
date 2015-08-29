package org.heat.world.controllers.events.roleplay.chat;

import org.heat.world.chat.WorldChannel;

public final class NewChannelEvent {
    private final WorldChannel channel;

    public NewChannelEvent(WorldChannel channel) {
        this.channel = channel;
    }

    public WorldChannel getChannel() {
        return channel;
    }
}
