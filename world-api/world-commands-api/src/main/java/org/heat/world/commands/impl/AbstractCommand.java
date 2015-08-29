package org.heat.world.commands.impl;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.heat.world.commands.Command;
import org.heat.world.commands.CommandAction;

import java.util.List;

/**
 * Managed by romain on 08/03/2015.
 */
@Getter
public class AbstractCommand implements Command {
    private final String name, syntax, description;
    private final List<String> aliases;
    private final CommandAction action;

    public AbstractCommand(String name,
                           String syntax,
                           String description,
                           List<String> aliases,
                           CommandAction action) {

        this.name = name;
        this.syntax = syntax;
        this.description = description;
        this.aliases = ImmutableList.copyOf(aliases);
        this.action = action;
    }
}