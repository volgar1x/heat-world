package org.heat.world.groups.events;

import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.WorldGroupGuest;
import org.heat.world.groups.WorldGroupMember;

public final class CancelGuestGroupEvent extends WorldGroupEvent {
    private final WorldGroupGuest guest;
    private final WorldGroupMember canceller;

    public CancelGuestGroupEvent(WorldGroup group, WorldGroupGuest guest, WorldGroupMember canceller) {
        super(group);
        this.guest = guest;
        this.canceller = canceller;
    }

    public WorldGroupGuest getGuest() {
        return guest;
    }

    public WorldGroupMember getCanceller() {
        return canceller;
    }
}
