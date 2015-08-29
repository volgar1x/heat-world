package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;

public final class NewNameGroupEvent extends WorldGroupEvent {
    private final String newName;

    public NewNameGroupEvent(WorldGroup group, String newName) {
        super(group);
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }
}
