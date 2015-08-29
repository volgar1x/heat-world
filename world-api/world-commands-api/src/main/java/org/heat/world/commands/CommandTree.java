package org.heat.world.commands;

import java.util.Map;

/**
 * Managed by romain on 11/03/2015.
 */
public interface CommandTree {
    String getName();
    Command getCommand();

    Map<String, CommandTree> getSubCommandTrees();
}
