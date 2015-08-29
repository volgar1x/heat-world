package org.heat.world.chat;

import com.github.blackrush.acara.EventBus;

import javax.inject.Provider;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class SharedChannelLookup implements WorldChannelLookup {
    private final Map<Integer, WorldChannel> channels;

    public SharedChannelLookup(Map<Integer, WorldChannel> channels) {
        this.channels = channels;
    }

    public SharedChannelLookup(Provider<EventBus> eventBusProvider, Clock clock, List<Integer> channelIds) {
        this.channels = new HashMap<>();
        for (Integer channelId : channelIds) {
            this.channels.put(channelId, new DedicatedWorldChannel(channelId, eventBusProvider.get(), clock));
        }
    }

    public SharedChannelLookup(List<WorldChannel> channels) {
        this.channels = new HashMap<>();
        for (WorldChannel channel : channels) {
            this.channels.put(channel.getChannelId(), channel);
        }
    }

    @Override
    public WorldChannel lookupChannel(WorldChannelMessage message) {
        return channels.get(message.getChannelId());
    }

    @Override
    public void forEach(Consumer<WorldChannel> fn) {
        channels.values().forEach(fn);
    }
}
