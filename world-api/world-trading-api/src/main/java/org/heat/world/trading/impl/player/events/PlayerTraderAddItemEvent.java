package org.heat.world.trading.impl.player.events;

import org.heat.world.items.WorldItem;
import org.heat.world.items.WorldItemWallet;
import org.heat.world.trading.impl.player.PlayerTrade;
import org.heat.world.trading.impl.player.PlayerTrader;

public final class PlayerTraderAddItemEvent extends PlayerTraderEvent {
    private final WorldItem item;

    public PlayerTraderAddItemEvent(PlayerTrade trade, PlayerTrader trader, WorldItemWallet wallet, WorldItem item) {
        super(trade, trader, wallet);
        this.item = item;
    }

    public WorldItem getItem() {
        return item;
    }
}
