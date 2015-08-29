package org.heat.world.trading.impl.player.events;

import org.heat.world.trading.impl.player.PlayerTrade;

public final class PlayerTradeEndEvent extends PlayerTradeEvent {

    public PlayerTradeEndEvent(PlayerTrade trade) {
        super(trade);
    }
}
