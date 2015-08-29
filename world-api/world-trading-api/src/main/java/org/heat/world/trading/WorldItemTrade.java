package org.heat.world.trading;

import org.heat.world.items.WorldItemBag;

import java.util.Optional;

public interface WorldItemTrade extends TradeInterface {
    @Override
    WorldItemBag getTradeBag(WorldTradeSide side);

    @Override
    Optional<? extends Result> conclude();

    public interface Result extends TradeInterface.Result {
        @Override
        WorldItemBag getFirst();

        @Override
        WorldItemBag getSecond();

        @Override
        default WorldItemBag getBag(WorldTradeSide side) {
            if (side == WorldTradeSide.FIRST) return getFirst();
            return getSecond();
        }
    }
}
