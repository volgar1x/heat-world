package org.heat.world.commands;

import org.heat.UserRank;

/**
 * Managed by romain on 06/04/2015.
 */
public class StdCommandValidations {
    public static CommandValidation empty() {
        return (s, e) -> {};
    }

    public static CommandValidation hasRank(UserRank rank) {
        return (s, e) -> e.unless(rank.enough(s.getRank()), "Le rang %s est nécessaire", rank.name());
    }
}
