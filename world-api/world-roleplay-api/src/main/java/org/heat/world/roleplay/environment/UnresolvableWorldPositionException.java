package org.heat.world.roleplay.environment;

public class UnresolvableWorldPositionException extends RuntimeException {
    public UnresolvableWorldPositionException() {
        super("the position is unresolvable");
    }

}
