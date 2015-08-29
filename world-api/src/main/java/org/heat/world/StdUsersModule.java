package org.heat.world;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import org.fungsi.concurrent.Worker;
import org.fungsi.concurrent.Workers;
import org.heat.shared.database.Table;
import org.heat.world.users.*;
import org.heat.world.users.jdbc.JdbcWorldUserRepository;
import org.heat.world.users.jdbc.WorldUserTable;

import java.util.concurrent.ExecutorService;

public class StdUsersModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(UserCapabilities.class).to(SimpleUserCapabilities.class).asEagerSingleton();

        bind(new TypeLiteral<Table<WorldUser>>(){}).to(WorldUserTable.class);

        expose(UserCapabilities.class);
        expose(WorldUserRepository.class);
    }

    @Provides
    Worker provideUserRepositoryWorker(ExecutorService executor) {
        return Workers.wrap(executor);
    }

    @Provides
    @Singleton
    WorldUserRepository provideWorldUserRepository(JdbcWorldUserRepository repository) {
        return new PermLazyWorldUserRepository(repository);
    }
}
