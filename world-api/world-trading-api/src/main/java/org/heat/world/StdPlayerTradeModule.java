package org.heat.world;

import com.github.blackrush.acara.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.heat.world.trading.impl.player.PlayerTradeFactory;
import org.heat.world.trading.impl.player.PlayerTrades;

import javax.inject.Provider;

public class StdPlayerTradeModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    PlayerTradeFactory provideFactory(Provider<EventBus> eventBusBuilder) {
        return PlayerTrades.createFactory(eventBusBuilder);
    }
}
