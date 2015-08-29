package org.heat.world.items;

import com.ankamagames.dofus.network.types.game.data.items.effects.ObjectEffect;

public interface WorldItemEffect {
    default <R> R visit(WorldItemEffectVisitor<R> visitor) {
        return visitor.otherwise(this);
    }

    ObjectEffect toObjectEffect();
}
