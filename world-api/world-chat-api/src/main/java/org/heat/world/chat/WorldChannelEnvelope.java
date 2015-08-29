package org.heat.world.chat;

import lombok.Value;

import java.time.Instant;

@Value
public final class WorldChannelEnvelope {
    final WorldSpeaker speaker;
    final WorldChannelMessage message;
    final Instant instant;
}
