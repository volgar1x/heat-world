package org.heat.world.items.effects;

import com.ankamagames.dofus.network.types.game.data.items.effects.ObjectEffect;
import lombok.RequiredArgsConstructor;
import org.heat.world.items.WorldItemEffect;

@RequiredArgsConstructor
public final class FallbackItemEffect implements WorldItemEffect {
    final ObjectEffect effect;

    @Override
    public ObjectEffect toObjectEffect() {
        return effect;
    }
}
