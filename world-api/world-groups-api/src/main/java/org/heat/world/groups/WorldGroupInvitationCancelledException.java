package org.heat.world.groups;

public class WorldGroupInvitationCancelledException extends RuntimeException {
    private final WorldGroupMember canceller;

    public WorldGroupInvitationCancelledException(WorldGroupMember canceller) {
        super("your invitation has been cancelled");
        this.canceller = canceller;
    }

    public WorldGroupMember getCanceller() {
        return canceller;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
