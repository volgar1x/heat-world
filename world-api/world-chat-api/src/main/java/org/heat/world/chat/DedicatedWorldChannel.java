package org.heat.world.chat;

import com.github.blackrush.acara.EventBus;

import java.time.Clock;
import java.time.Instant;
import java.util.function.BiPredicate;

public final class DedicatedWorldChannel implements VirtualWorldChannel {
    private final int channelId;
    private final EventBus eventBus;
    private final Clock clock;
    private final BiPredicate<WorldChannel, WorldSpeaker> predicate;

    public DedicatedWorldChannel(int channelId, EventBus eventBus, Clock clock) {
        this(channelId, eventBus, clock, (ch, spkr) -> true);
    }

    public DedicatedWorldChannel(int channelId, EventBus eventBus, Clock clock, BiPredicate<WorldChannel, WorldSpeaker> predicate) {
        this.channelId = channelId;
        this.eventBus = eventBus;
        this.clock = clock;
        this.predicate = predicate;
    }

    @Override
    public int getChannelId() {
        return channelId;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Instant timestamp() {
        return clock.instant();
    }

    @Override
    public boolean accepts(WorldSpeaker speaker) {
        return predicate.test(this, speaker);
    }
}
