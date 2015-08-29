package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.WorldGroupGuest;

public final class NewGuestGroupEvent extends WorldGroupEvent {
    private final WorldGroupGuest guest;

    public NewGuestGroupEvent(WorldGroup group, WorldGroupGuest guest) {
        super(group);
        this.guest = guest;
    }

    public WorldGroupGuest getGuest() {
        return guest;
    }
}
