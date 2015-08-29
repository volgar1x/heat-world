package org.heat.world.commands.impl;

import org.heat.world.commands.CommandValidation;

/**
 * Managed by romain on 06/04/2015.
 */
public class CommandErrors implements CommandValidation.Errors{
    private final StringBuilder builder;
    private String errors;

    public CommandErrors() {
        this.builder = new StringBuilder();
    }

    @Override
    public void add(String msg) {
        if(builder.length() > 0)
            builder.append("\n");
        builder.append("- ").append(msg);
    }

    @Override
    public void add(String format, Object... args) {
        add(String.format(format, args));
    }

    @Override
    public void unless(boolean b, String msg) {
        if(b) add(msg);
    }

    @Override
    public void unless(boolean b, String format, Object... args) {
        if(b) add(format, args);
    }

    @Override
    public String toString() {
        if(errors == null)
            errors = builder.toString();
        return errors;
    }

    @Override
    public boolean isEmpty() {
        return builder.length() == 0;
    }

    public static CommandValidation.Errors newErrors() {
        return new CommandErrors();
    }
}
