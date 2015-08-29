package org.heat.world.trading;

import org.heat.world.items.WorldItemWallet;

public interface WorldTrader extends WorldItemTrader, WorldKamasTrader {
    @Override
    WorldItemWallet getTraderBag();
}
