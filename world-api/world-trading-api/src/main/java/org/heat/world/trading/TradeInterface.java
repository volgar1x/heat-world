package org.heat.world.trading;

import com.github.blackrush.acara.EventBus;
import org.heat.world.items.WorldBag;

import java.util.Optional;

public interface TradeInterface {
    EventBus getEventBus();

    Optional<? extends Result> conclude();
    boolean isConcluded();

    WorldBag getTradeBag(WorldTradeSide side);

    public interface Result {
        WorldBag getFirst();
        WorldBag getSecond();

        default WorldBag getBag(WorldTradeSide side) {
            if (side == WorldTradeSide.FIRST) return getFirst();
            return getSecond();
        }
    }
}
