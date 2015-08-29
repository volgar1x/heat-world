package org.heat.world.commands.builder;

/**
 * Managed by romain on 14/03/2015.
 */
public class CommandBuildingException extends RuntimeException {
    public CommandBuildingException(String reason, Object... args) {
        super(String.format(reason, args));
    }
}
