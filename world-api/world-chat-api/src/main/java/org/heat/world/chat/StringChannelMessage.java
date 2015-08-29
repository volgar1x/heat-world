package org.heat.world.chat;

public class StringChannelMessage implements WorldChannelMessage {
    private final int channelId;
    private final String string;

    public StringChannelMessage(int channelId, String string) {
        this.channelId = channelId;
        this.string = string;
    }

    @Override
    public int getChannelId() {
        return channelId;
    }

    @Override
    public String getString() {
        return string;
    }
}
