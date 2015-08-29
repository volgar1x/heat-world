package org.heat.world.trading.impl.player;

import org.heat.world.items.WorldItemWallet;
import org.heat.world.trading.WorldTrade;
import org.heat.world.trading.WorldTradeSide;

import java.util.Optional;

public interface PlayerTrade extends WorldTrade {
    int getStep();
    void increaseStep();

    @Override
    Optional<? extends Result> conclude();
    void cancel(WorldTradeSide side);
    boolean isCancelled();

    void check(WorldTradeSide side);
    void uncheck(WorldTradeSide side);

    void uncheckAllIfNeeded();

    PlayerTrader getTrader(WorldTradeSide side);

    @Override
    WorldItemWallet getTradeBag(WorldTradeSide side);

    default PlayerTrader getFirstTrader() {
        return getTrader(WorldTradeSide.FIRST);
    }

    default PlayerTrader getSecondTrader() {
        return getTrader(WorldTradeSide.SECOND);
    }
}
