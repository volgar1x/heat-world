package org.heat.world.players.guilds;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import org.fungsi.concurrent.Worker;
import org.fungsi.concurrent.Workers;
import org.heat.shared.database.Table;
import org.heat.world.guilds.WorldGuildMemberRepository;
import org.heat.world.players.Player;

import java.util.concurrent.ExecutorService;

public class PlayerGuildModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(WorldGuildMemberRepository.class).to(PlayerGuildMemberRepository.class).in(Scopes.SINGLETON);
        bind(new TypeLiteral<Table<Player>>(){}).to(PlayerGuildMemberTable.class).in(Scopes.SINGLETON);

        expose(WorldGuildMemberRepository.class);
    }

    @Provides
    Worker provideWorker(ExecutorService worker) {
        return Workers.wrap(worker);
    }
}
