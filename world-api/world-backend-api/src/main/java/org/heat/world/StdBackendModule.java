package org.heat.world;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.fungsi.concurrent.Timer;
import org.fungsi.concurrent.Timers;
import org.heat.world.backend.Backend;
import org.heat.world.backend.BackendUserRepository;
import org.heat.world.backend.BackendUserRepositoryImpl;
import org.heat.world.backend.DefaultBackend;
import org.heat.world.users.UserRepository;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

public class StdBackendModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(Random.class).annotatedWith(Names.named("ticket")).toInstance(new Random(System.nanoTime()));
        bind(Backend.class).to(DefaultBackend.class).asEagerSingleton();
        bind(BackendUserRepository.class).to(BackendUserRepositoryImpl.class).asEagerSingleton();
        bind(UserRepository.class).to(BackendUserRepository.class);
        expose(Backend.class);
        expose(BackendUserRepository.class);
        expose(UserRepository.class);
    }

    @Provides
    @Singleton
    Timer provideUserAuthTtl(ScheduledExecutorService executor) {
        return Timers.wrap(executor);
    }
}
