package org.heat.world.items;

import org.heat.world.items.effects.IntegerItemEffect;

public interface WorldItemEffectVisitor<R> {
    R visitInteger(IntegerItemEffect effect);
    R otherwise(WorldItemEffect effect);
}
