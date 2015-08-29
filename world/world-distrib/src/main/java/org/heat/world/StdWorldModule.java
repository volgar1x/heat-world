package org.heat.world;

import com.github.blackrush.acara.Acara;
import com.github.blackrush.acara.EventBus;
import com.github.blackrush.acara.JavaEventMetadataBuilder;
import com.github.blackrush.acara.JavaListenerBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import org.fungsi.concurrent.Workers;
import org.rocket.network.acara.RocketAcara;
import org.rocket.world.StdBackendControllerModule;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class StdWorldModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new StdWorldSystemModule());

        install(new StdDistBackendModule(new StdBackendControllerModule()));
        install(new StdDistFrontendModule(new StdFrontendControllerModule()));
        install(new StdDatacenterModule());
        install(new StdBackendModule());
        install(new StdJdbcModule());
        install(new StdUsersModule());
        install(new StdPlayersModule());
        install(new StdWorldEnvironmentModule());
        install(new StdItemsModule());
        install(new StdPlayerTradeModule());
        install(new StdClassicalGroupModule());
        install(new StdFrontendCommandsModule());
        install(new StdClassicalGuildModule());
    }

    @Provides
    @Singleton
    ExecutorService provideExecutorService(Config config) {
        int parallelism = config.getInt("heat.world.workers-parallelism");
        if (parallelism <= 0) {
            parallelism = Runtime.getRuntime().availableProcessors();
        }
        return Executors.newWorkStealingPool(parallelism);
    }

    @Provides
    @Singleton
    ScheduledExecutorService provideScheduler(Config config) {
        int coreSize = config.getInt("heat.world.scheduler-core-size");
        return Executors.newScheduledThreadPool(coreSize);
    }

    @Provides
    EventBus provideEventBusBuilder(ExecutorService executor) {
        return Acara.newEventBus(RocketAcara.events().withFallback(new JavaEventMetadataBuilder()),
                                 RocketAcara.listeners().concat(new JavaListenerBuilder()),
                                 Workers.wrap(executor));
    }

    @Provides
    @Singleton
    ByteBufAllocator provideByteBufAllocator() {
        return new PooledByteBufAllocator(/*preferDirect*/true);
    }

    @Provides
    Clock provideClock() {
        return Clock.systemUTC();
    }
}
