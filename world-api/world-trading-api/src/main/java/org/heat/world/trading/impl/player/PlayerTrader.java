package org.heat.world.trading.impl.player;

import org.heat.world.items.WorldItemWallet;
import org.heat.world.trading.WorldTrader;

public interface PlayerTrader extends WorldTrader {
    @Deprecated
    @Override
    default WorldItemWallet getTraderBag() {
        throw new UnsupportedOperationException();
    }
}
