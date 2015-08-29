package org.heat.world.players.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Managed by romain on 08/03/2015.
 */

@Getter
@RequiredArgsConstructor
public final class NoticeAdminEvent {
    private final byte type;
    private final String text;
}
