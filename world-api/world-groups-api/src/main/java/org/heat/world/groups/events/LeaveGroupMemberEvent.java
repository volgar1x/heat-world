package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.WorldGroupMember;

public final class LeaveGroupMemberEvent extends WorldGroupMemberEvent {
    public LeaveGroupMemberEvent(WorldGroup group, WorldGroupMember member) {
        super(group, member);
    }
}
