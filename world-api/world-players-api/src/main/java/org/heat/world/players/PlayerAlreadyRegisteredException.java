package org.heat.world.players;

public class PlayerAlreadyRegisteredException extends RuntimeException {
    public PlayerAlreadyRegisteredException(String message) {
        super(message);
    }

    public PlayerAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
