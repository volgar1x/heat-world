package org.heat.world.commands.builder;

import org.heat.world.commands.CommandArg;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Managed by romain on 25/03/2015.
 */
public class ArgsBuilder {
    private final Map<String, CommandArg> arguments = new LinkedHashMap<>();

    public static ArgsBuilder start() {
        return new ArgsBuilder();
    }

    public Map<String, CommandArg> build() {
        return arguments;
    }

    public ArgsBuilder addArg(String label, String... possibilities) {
        return addArg(label, false, false, possibilities);
    }

    public ArgsBuilder addRequiredArg(String label, String... possibilities) {
        return addArg(label, true, false, possibilities);
    }

    public ArgsBuilder addSpacedArg(String label, String... possibilities) {
        return addArg(label, false, true, possibilities);
    }

    public ArgsBuilder addRequiredSpacedArg(String label, String... possibilities) {
        return addArg(label, true, true, possibilities);
    }

    private ArgsBuilder addArg(String l, boolean r, boolean w, String... p) {
        arguments.put(l, new CommandArg(l, p, r, w));

        return this;
    }
}
