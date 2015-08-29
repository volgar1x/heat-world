package org.heat.world.trading;

import org.heat.world.items.WorldWallet;

public interface WorldKamasTrader extends TraderInterface {
    @Override
    WorldWallet getTraderBag();
}
