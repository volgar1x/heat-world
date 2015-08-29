package org.heat.world.roleplay.environment;

public class UnresolvedWorldPositionException extends RuntimeException {
    public UnresolvedWorldPositionException() {
        super("this position is unresolved");
    }

}
