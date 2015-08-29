package org.heat.world.trading;

public enum WorldTradeSide {
    FIRST,
    SECOND,
    ;

    public WorldTradeSide backwards() {
        if (this == FIRST) return SECOND;
        return FIRST;
    }
}
