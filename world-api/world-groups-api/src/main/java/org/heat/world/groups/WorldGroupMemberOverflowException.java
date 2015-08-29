package org.heat.world.groups;

public class WorldGroupMemberOverflowException extends RuntimeException {
    public WorldGroupMemberOverflowException() {
        super("there is not anymore room in this group");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
