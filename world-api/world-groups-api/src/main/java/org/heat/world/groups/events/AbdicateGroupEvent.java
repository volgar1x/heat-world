package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.WorldGroupMember;

public final class AbdicateGroupEvent extends WorldGroupEvent {
    private final WorldGroupMember newLeader;

    public AbdicateGroupEvent(WorldGroup group, WorldGroupMember newLeader) {
        super(group);
        this.newLeader = newLeader;
    }

    public WorldGroupMember getNewLeader() {
        return newLeader;
    }
}
