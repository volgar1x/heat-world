package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.WorldGroupMember;

public abstract class WorldGroupMemberEvent extends WorldGroupEvent {
    private final WorldGroupMember member;

    protected WorldGroupMemberEvent(WorldGroup group, WorldGroupMember member) {
        super(group);
        this.member = member;
    }

    public WorldGroupMember getMember() {
        return member;
    }
}
