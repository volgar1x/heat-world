package org.heat.world.players.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Managed by romain on 08/04/2015.
 */
@Getter
@RequiredArgsConstructor
public final class InformPlayerEvent {
    private final int id;
    private final String[] args;
}
