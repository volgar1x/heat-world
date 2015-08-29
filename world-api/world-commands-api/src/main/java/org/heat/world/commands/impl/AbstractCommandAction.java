package org.heat.world.commands.impl;

import org.heat.shared.stream.ImmutableCollectors;
import org.heat.world.commands.CommandAction;
import org.heat.world.commands.CommandArg;
import org.heat.world.commands.builder.ArgsBuilder;

import java.util.Map;
import java.util.function.Function;

/**
 * Managed by romain on 23/03/2015.
 */
public abstract class AbstractCommandAction implements CommandAction {
    private Map<String, CommandArg> arguments, required;

    protected abstract ArgsBuilder getArguments(ArgsBuilder b);

    @Override
    public Map<String, CommandArg> getArguments() {
        if(arguments == null)
            arguments = getArguments(ArgsBuilder.start()).build();
        return arguments;
    }

    @Override
    public Map<String, CommandArg> getRequiredArguments() {
        if(required == null)
            required = getArguments().values().stream()
                             .filter(CommandArg::isRequired)
                             .collect(ImmutableCollectors.toMap(CommandArg::getLabel, Function.identity()));
        return required;
    }
}
