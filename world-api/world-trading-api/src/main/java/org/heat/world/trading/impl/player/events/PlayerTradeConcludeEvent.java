package org.heat.world.trading.impl.player.events;

import org.heat.world.trading.WorldTrade;
import org.heat.world.trading.impl.player.PlayerTrade;

public final class PlayerTradeConcludeEvent extends PlayerTradeEvent {
    private final PlayerTrade.Result result;

    public PlayerTradeConcludeEvent(PlayerTrade trade, WorldTrade.Result result) {
        super(trade);
        this.result = result;
    }

    public PlayerTrade.Result getResult() {
        return result;
    }
}
