package org.heat.world.chat;

import org.heat.world.items.WorldItem;

import java.util.List;

public final class ChannelMessageWithAttachments implements WorldChannelMessage {
    private final WorldChannelMessage message;
    private final List<WorldItem> attachments;

    public ChannelMessageWithAttachments(WorldChannelMessage message, List<WorldItem> attachments) {
        this.message = message;
        this.attachments = attachments;
    }

    @Override
    public int getChannelId() {
        return message.getChannelId();
    }

    @Override
    public String getString() {
        return message.getString();
    }

    public List<WorldItem> getAttachments() {
        return attachments;
    }
}
