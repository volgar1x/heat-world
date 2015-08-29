package org.heat.world.commands;

/**
 * Managed by romain on 06/04/2015.
 */
@FunctionalInterface
public interface CommandValidation {
    interface Errors {
        void add(String msg);
        void add(String format, Object... args);
        void unless(boolean b, String msg);
        void unless(boolean b, String format, Object... args);
        String toString();
        boolean isEmpty();
    }

    void validate(CommandSender sender, Errors errors);

    static CommandValidation of(CommandValidation... validations) {
        return (s, e) -> {
            for(CommandValidation val: validations)
                val.validate(s,e);
        };
    }
}
