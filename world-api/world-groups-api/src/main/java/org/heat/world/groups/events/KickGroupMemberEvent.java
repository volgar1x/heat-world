package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.WorldGroupMember;

public final class KickGroupMemberEvent extends WorldGroupMemberEvent {
    private final WorldGroupMember kicker;

    public KickGroupMemberEvent(WorldGroup group, WorldGroupMember member, WorldGroupMember kicker) {
        super(group, member);
        this.kicker = kicker;
    }

    public WorldGroupMember getKicker() {
        return kicker;
    }
}
