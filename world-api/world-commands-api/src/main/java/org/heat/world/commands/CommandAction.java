package org.heat.world.commands;

import java.util.Map;

/**
 * Managed by romain on 08/03/2015.
 */
public interface CommandAction {
    Map<String, CommandArg> getArguments();
    Map<String, CommandArg> getRequiredArguments();
    CommandValidation getValidation();

    void execute(CommandSender sender, boolean console, String[] args);
}