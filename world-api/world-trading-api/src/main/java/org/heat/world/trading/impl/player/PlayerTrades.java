package org.heat.world.trading.impl.player;

import com.github.blackrush.acara.EventBus;

import javax.inject.Provider;

public final class PlayerTrades {
    private PlayerTrades() {}

    public static PlayerTrade create(EventBus eventBus, PlayerTrader first, PlayerTrader second) {
        return new PlayerTradeImpl(eventBus, first, second);
    }

    // Y U NO CURRY JAVA
    public static PlayerTradeFactory createFactory(Provider<EventBus> eventBusBuilder) {
        return (first, second) -> create(eventBusBuilder.get(), first, second);
    }
}
