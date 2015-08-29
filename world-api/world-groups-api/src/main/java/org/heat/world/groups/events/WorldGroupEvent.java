package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;

public abstract class WorldGroupEvent {
    private final WorldGroup group;

    protected WorldGroupEvent(WorldGroup group) {
        this.group = group;
    }

    public WorldGroup getGroup() {
        return group;
    }
}
