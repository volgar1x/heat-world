package org.heat.world.trading.impl.player;

import java.util.function.BiFunction;

public interface PlayerTradeFactory extends BiFunction<PlayerTrader, PlayerTrader, PlayerTrade> {}
