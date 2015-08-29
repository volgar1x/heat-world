package org.heat.world.commands;

import java.util.List;

/**
 * Managed by romain on 08/03/2015.
 */
public interface Command {
    String getName();
    String getDescription();
    String getSyntax();

    CommandAction getAction();

    List<String> getAliases();
}