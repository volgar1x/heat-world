package org.heat.world.commands;

/**
 * Managed by romain on 08/03/2015.
 */
public interface CommandManager {
    boolean execute(CommandSender sender, String message, boolean withoutPrefix);
    void forward(CommandSender sender, String command, String... args);
}