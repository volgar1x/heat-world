package org.heat.login;

import com.github.blackrush.acara.Acara;
import com.github.blackrush.acara.EventBus;
import com.github.blackrush.acara.JavaEventMetadataBuilder;
import com.github.blackrush.acara.JavaListenerBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import org.fungsi.concurrent.Workers;
import org.rocket.network.acara.RocketAcara;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StdLoginModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new StdDistBackendModule());
        install(new StdDistFrontendModule());
        install(new StdJdbcModule());
        install(new StdUsersModule());
        install(new StdJdbcUsersModule());
    }

    @Provides
    ExecutorService provideEventBusExecutor() {
        return Executors.newFixedThreadPool(16);
    }

    @Provides
    EventBus provideEventBusBuilder(ExecutorService executor) {
        return Acara.newEventBus(
                RocketAcara.events().withFallback(new JavaEventMetadataBuilder()),
                RocketAcara.listeners().concat(new JavaListenerBuilder()),
                Workers.wrap(executor)
        );
    }

    @Provides
    @Singleton
    ByteBufAllocator provideByteBufAllocator() {
        return new PooledByteBufAllocator(/*preferDirect*/false);
    }
}
