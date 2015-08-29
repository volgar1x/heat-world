package org.heat.world.commands.impl;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.heat.world.commands.Command;
import org.heat.world.commands.CommandTree;

import java.util.Map;

/**
 * Managed by romain on 11/03/2015.
 */
@Getter
public class DefaultCommandTree implements CommandTree {
    private final String name;
    private final Command command;
    private final Map<String, CommandTree> subCommandTrees;

    public DefaultCommandTree(String name,
                              Command command,
                              Map<String, CommandTree> subCommandTrees)
    {
        this.name = name;
        this.command = command;
        this.subCommandTrees = ImmutableMap.copyOf(subCommandTrees);
    }
}
