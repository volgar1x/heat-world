package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;

public final class DisbandGroupEvent extends WorldGroupEvent {
    public DisbandGroupEvent(WorldGroup group) {
        super(group);
    }
}
