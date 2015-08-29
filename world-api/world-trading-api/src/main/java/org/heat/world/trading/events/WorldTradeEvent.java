package org.heat.world.trading.events;

import org.heat.world.trading.TradeInterface;

public abstract class WorldTradeEvent {
    public abstract TradeInterface getTrade();
}
