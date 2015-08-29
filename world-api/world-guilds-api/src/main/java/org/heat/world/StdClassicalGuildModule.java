package org.heat.world;

import com.google.inject.PrivateModule;
import com.google.inject.Scopes;
import org.heat.world.guilds.WorldGuildFactory;
import org.heat.world.guilds.WorldGuildRepository;
import org.heat.world.guilds.impl.ClassicalGuildFactory;
import org.heat.world.guilds.impl.JdbcGuildModule;

public final class StdClassicalGuildModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(WorldGuildFactory.class).to(ClassicalGuildFactory.class).in(Scopes.SINGLETON);
        install(new JdbcGuildModule());

        expose(WorldGuildFactory.class);
        expose(WorldGuildRepository.class);
    }
}
