package org.heat.world.trading.events;

import org.heat.world.items.WorldItem;
import org.heat.world.trading.WorldItemTrade;
import org.heat.world.trading.WorldItemTrader;

public abstract class WorldItemTradeEvent extends WorldTradeEvent {
    private final WorldItem item;

    protected WorldItemTradeEvent(WorldItem item) {
        this.item = item;
    }

    @Override
    public abstract WorldItemTrade getTrade();

    public abstract WorldItemTrader getTrader();

    public WorldItem getItem() {
        return item;
    }
}
