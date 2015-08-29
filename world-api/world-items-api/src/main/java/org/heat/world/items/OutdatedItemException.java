package org.heat.world.items;

public class OutdatedItemException extends RuntimeException {
    private final WorldItem outdated;
    private final WorldItem updated;

    public OutdatedItemException(WorldItem left, WorldItem right) {
        if (left.getVersion() < right.getVersion()) {
            this.outdated = left;
            this.updated = right;
        } else {
            this.outdated = right;
            this.updated = left;
        }
    }

    public WorldItem getOutdated() {
        return outdated;
    }

    public WorldItem getUpdated() {
        return updated;
    }

    @Override
    public String getMessage() {
        return outdated + " is a outdated\nhere is its updated version " + updated;
    }
}
