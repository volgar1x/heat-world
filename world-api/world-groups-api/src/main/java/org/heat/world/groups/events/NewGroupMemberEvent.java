package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.WorldGroupMember;

public final class NewGroupMemberEvent extends WorldGroupMemberEvent {
    public NewGroupMemberEvent(WorldGroup group, WorldGroupMember member) {
        super(group, member);
    }
}
