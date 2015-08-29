package org.heat.world.trading;

import org.heat.world.items.WorldItemBag;

public interface WorldItemTrader extends TraderInterface {
    @Override
    WorldItemBag getTraderBag();
}
