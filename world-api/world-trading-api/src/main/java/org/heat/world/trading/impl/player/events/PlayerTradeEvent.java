package org.heat.world.trading.impl.player.events;

import org.heat.world.trading.events.WorldTradeEvent;
import org.heat.world.trading.impl.player.PlayerTrade;

public abstract class PlayerTradeEvent extends WorldTradeEvent {
    private final PlayerTrade trade;

    protected PlayerTradeEvent(PlayerTrade trade) {
        this.trade = trade;
    }

    @Override
    public PlayerTrade getTrade() {
        return trade;
    }
}
