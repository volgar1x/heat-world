package org.heat.world.items;

import com.ankamagames.dofus.datacenter.effects.EffectInstance;
import com.ankamagames.dofus.datacenter.effects.instances.EffectInstanceDice;
import com.ankamagames.dofus.datacenter.effects.instances.EffectInstanceInteger;
import com.ankamagames.dofus.network.types.game.data.items.effects.ObjectEffect;
import com.ankamagames.dofus.network.types.game.data.items.effects.ObjectEffectDice;
import com.ankamagames.dofus.network.types.game.data.items.effects.ObjectEffectInteger;
import org.heat.world.items.effects.FallbackItemEffect;
import org.heat.world.items.effects.IntegerItemEffect;

import java.util.concurrent.ThreadLocalRandom;

final class Effects {
    private Effects() {}

    public static WorldItemEffect fromObjectEffect(ObjectEffect effect) {
        if (effect instanceof ObjectEffectInteger) {
            return new IntegerItemEffect(effect.actionId, ((ObjectEffectInteger) effect).value);
        }

        return new FallbackItemEffect(effect);
    }

    public static WorldItemEffect fromEffectInstance(EffectInstance effect) {
        if (effect instanceof EffectInstanceDice) {
            EffectInstanceDice dice = (EffectInstanceDice) effect;

            short value;
            if (dice.getDiceSide() == 0) {
                value = (short) dice.getDiceNum();
            } else {
                value = (short) ThreadLocalRandom.current().nextInt(dice.getDiceNum(), dice.getDiceSide());
            }

            return new IntegerItemEffect((short) effect.getEffectId(), value);
        }

        if (effect instanceof EffectInstanceInteger) {
            return new IntegerItemEffect((short) effect.getEffectId(), (short) ((EffectInstanceInteger) effect).getValue());
        }

        return new FallbackItemEffect(toObjectEffect(effect));
    }

    public static ObjectEffect toObjectEffect(EffectInstance effect) {
        if (effect instanceof EffectInstanceDice) {
            short num = (short) ((EffectInstanceDice) effect).getDiceNum();
            short side = (short) ((EffectInstanceDice) effect).getDiceSide();
            short constt = (short) 0;
            if (side == 0) {
                constt = num;
                num = (short) 0;
            }
            return new ObjectEffectDice(
                    (short) effect.getEffectId(),
                    num,
                    side,
                    constt
            );
        }
        if (effect instanceof EffectInstanceInteger) {
            return new ObjectEffectInteger(
                    (short) effect.getEffectId(),
                    (short) ((EffectInstanceInteger) effect).getValue()
            );
        }

        throw new IllegalArgumentException();
    }
}
