package org.heat.world.trading.impl.player.events;

import org.heat.world.trading.impl.player.PlayerTrade;
import org.heat.world.trading.impl.player.PlayerTrader;

public final class PlayerTradeCheckEvent extends PlayerTradeEvent {
    private final PlayerTrader trader;
    private final boolean check;

    public PlayerTradeCheckEvent(PlayerTrade trade, PlayerTrader trader, boolean check) {
        super(trade);
        this.trader = trader;
        this.check = check;
    }

    public PlayerTrader getTrader() {
        return trader;
    }

    public boolean isCheck() {
        return check;
    }
}
