package org.heat.world.chat;

import com.github.blackrush.acara.EventBus;
import com.github.blackrush.acara.Subscribable;

import java.time.Instant;

public interface VirtualWorldChannel extends WorldChannel {
    /**
     * Get {@code EventBus} used to publish and subscribe channel message and listeners
     * @return a non-null event bus
     */
    EventBus getEventBus();

    /**
     * Timestamp a message
     * @return a non-null instant
     */
    default Instant timestamp() {
        return Instant.now();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void speak(WorldSpeaker speaker, WorldChannelMessage message) {
        getEventBus().publish(new WorldChannelEnvelope(speaker, message, timestamp()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default Subscribable getSubscribableChannelView() {
        return getEventBus();
    }
}
