package org.heat.world.trading.impl.player.events;

import org.heat.world.items.WorldItemWallet;
import org.heat.world.trading.impl.player.PlayerTrade;
import org.heat.world.trading.impl.player.PlayerTrader;

public final class PlayerTraderKamasEvent extends PlayerTraderEvent {
    public PlayerTraderKamasEvent(PlayerTrade trade, PlayerTrader trader, WorldItemWallet wallet) {
        super(trade, trader, wallet);
    }
}
