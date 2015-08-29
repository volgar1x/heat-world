package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.WorldGroupGuest;

public final class RemoveGuestGroupEvent extends WorldGroupEvent {
    private final WorldGroupGuest guest;

    public RemoveGuestGroupEvent(WorldGroup group, WorldGroupGuest guest) {
        super(group);
        this.guest = guest;
    }

    public WorldGroupGuest getGuest() {
        return guest;
    }
}
