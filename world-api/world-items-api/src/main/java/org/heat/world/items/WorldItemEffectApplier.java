package org.heat.world.items;

import org.fungsi.Unit;
import org.heat.world.items.effects.IntegerItemEffect;
import org.heat.world.metrics.GameStatBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;
import static org.fungsi.Unit.unit;

public class WorldItemEffectApplier extends NullWorldItemEffectVisitor<Unit> {
    public static final Logger log = LoggerFactory.getLogger(WorldItemEffectApplier.class);

    private final WorldItem item;
    private final GameStatBook book;
    private final boolean applying;

    public WorldItemEffectApplier(GameStatBook book, WorldItem item, boolean applying) {
        this.item = requireNonNull(item, "item");
        this.book = requireNonNull(book, "book");
        this.applying = applying;
    }

    @Override
    public Unit visitInteger(IntegerItemEffect effect) {
        WorldItemEffectAction action = WorldItemEffectAction.valueOf(effect.getActionId());

        if (action.stat != null) {
            short modifier = effect.getValue();
            if (modifier < 0) {
                log.warn("I got a modifier strictly negative ({}) of {} from {}", modifier, action, item);
            }

            if ((applying && !action.addStat) || (!applying && action.addStat)) {
                modifier = (short) -modifier;
            }

            book.get(action.stat).plusEquipment(modifier);
        } else {
            log.warn("I wanted to apply {} given {} but couldn't find a matching stat {}", action, effect, item);
        }

        return unit();
    }

    @Override
    public Unit otherwise(WorldItemEffect effect) {
        log.warn("I wasn't able to apply {} :-( from {}", effect, item);
        return unit();
    }
}
