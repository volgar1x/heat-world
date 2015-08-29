package org.heat.world.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Managed by romain on 08/03/2015.
 */

@Getter
@RequiredArgsConstructor
public final class CommandArg {
    private final String label;
    private final String[] staticArgs;
    private final boolean required;
    private final boolean withSpaces;
}