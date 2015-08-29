package org.heat.world.guilds.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import org.fungsi.concurrent.Worker;
import org.fungsi.concurrent.Workers;
import org.heat.shared.database.Table;
import org.heat.world.guilds.WorldGuild;
import org.heat.world.guilds.WorldGuildRepository;

import java.util.concurrent.ExecutorService;

public final class JdbcGuildModule extends AbstractModule {
    private static final TypeLiteral<Table<WorldGuild>> GUILD_TABLE_KEY = new TypeLiteral<Table<WorldGuild>>() {};

    @Override
    protected void configure() {
        bind(GUILD_TABLE_KEY).to(ClassicalGuildTable.class).in(Scopes.SINGLETON);
        bind(WorldGuildRepository.class).to(JdbcGuildRepository.class).in(Scopes.SINGLETON);
    }

    @Provides
    Worker provideWorker(ExecutorService worker) {
        return Workers.wrap(worker);
    }
}
