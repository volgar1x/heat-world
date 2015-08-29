package org.heat.world;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.fungsi.concurrent.Worker;
import org.fungsi.concurrent.Workers;
import org.heat.shared.database.Table;
import org.heat.world.players.*;
import org.heat.world.players.contacts.ContactRepository;
import org.heat.world.players.contacts.JdbcContactsModule;
import org.heat.world.players.items.JdbcPlayerItemRepository;
import org.heat.world.players.items.PlayerItemRepository;
import org.heat.world.players.notifications.JdbcPlayerNotifRepository;
import org.heat.world.players.notifications.PlayerNotifRepository;
import org.heat.world.players.shortcuts.JdbcPlayerShortcutRepository;
import org.heat.world.players.shortcuts.PlayerShortcut;
import org.heat.world.players.shortcuts.PlayerShortcutRepository;
import org.heat.world.players.shortcuts.PlayerShortcutTable;

import java.util.concurrent.ExecutorService;

public class StdJdbcPlayersModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<Table<Player>>() {}).to(PlayerTable.class);
        bind(new TypeLiteral<Table<PlayerShortcut>>() {}).to(PlayerShortcutTable.class);

        bind(PlayerItemRepository.class).to(JdbcPlayerItemRepository.class).asEagerSingleton();
        bind(PlayerNotifRepository.class).to(JdbcPlayerNotifRepository.class).asEagerSingleton();

        bind(PlayerShortcutRepository.class).to(JdbcPlayerShortcutRepository.class).asEagerSingleton();

        bind(PlayerRepository.class).annotatedWith(Names.named("base")).to(JdbcPlayerRepository.class).asEagerSingleton();
        bind(PlayerRepository.class).to(PermLazyPlayerRepository.class).asEagerSingleton();

        install(new JdbcContactsModule());

        expose(PlayerRepository.class);
        expose(PlayerItemRepository.class);
        expose(PlayerNotifRepository.class);
        expose(PlayerShortcutRepository.class);
        expose(ContactRepository.class);
    }

    @Provides
    Worker providePlayerRepositoryWorker(ExecutorService executor) {
        return Workers.wrap(executor);
    }
}
