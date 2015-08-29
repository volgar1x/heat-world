package org.heat.world.chat;

import java.util.function.Consumer;

public interface WorldChannelLookup {
    /**
     * Lookup a channel given a message
     * @param message a non-null message
     * @return a <b>nullable</b> channel
     */
    WorldChannel lookupChannel(WorldChannelMessage message);

    /**
     * Consume a channel
     * @param fn a non-null consumer
     */
    void forEach(Consumer<WorldChannel> fn);

    /**
     * Compose this lookup with another one
     * @param fallback a non-null fallback
     * @return a non-null lookup
     */
    default WorldChannelLookup andThen(final WorldChannelLookup fallback) {
        final WorldChannelLookup self = this;
        return new WorldChannelLookup() {
            @Override
            public WorldChannel lookupChannel(WorldChannelMessage message) {
                WorldChannel channel = self.lookupChannel(message);
                if (channel == null) {
                    channel = fallback.lookupChannel(message);
                }
                return channel;
            }

            @Override
            public void forEach(Consumer<WorldChannel> fn) {
                self.forEach(fn);
                fallback.forEach(fn);
            }
        };
    }
}
