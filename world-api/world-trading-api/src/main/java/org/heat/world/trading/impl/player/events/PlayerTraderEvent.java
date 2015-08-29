package org.heat.world.trading.impl.player.events;

import org.heat.world.items.WorldItemWallet;
import org.heat.world.trading.impl.player.PlayerTrade;
import org.heat.world.trading.impl.player.PlayerTrader;

public abstract class PlayerTraderEvent extends PlayerTradeEvent {
    private final PlayerTrader trader;
    private final WorldItemWallet wallet;

    protected PlayerTraderEvent(PlayerTrade trade, PlayerTrader trader, WorldItemWallet wallet) {
        super(trade);
        this.trader = trader;
        this.wallet = wallet;
    }

    public PlayerTrader getTrader() {
        return trader;
    }

    public WorldItemWallet getWallet() {
        return wallet;
    }
}
