package org.heat.world;

import com.ankamagames.dofus.network.ProtocolTypeManager;
import com.ankamagames.dofus.network.types.game.data.items.effects.ObjectEffect;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.fungsi.concurrent.Worker;
import org.fungsi.concurrent.Workers;
import org.heat.dofus.network.NetworkComponentFactory;
import org.heat.shared.database.Table;
import org.heat.world.items.*;

import java.util.concurrent.ExecutorService;

public class StdItemsModule extends PrivateModule {

    public static final TypeLiteral<Table<WorldItem>> ITEM_TABLE = new TypeLiteral<Table<WorldItem>>(){};

    @Override
    protected void configure() {
        bind(ITEM_TABLE).to(WorldItemTable.class).asEagerSingleton();
        bind(WorldItemFactory.class).to(DefaultItemFactory.class).asEagerSingleton();
        bind(WorldItemRepository.class).annotatedWith(Names.named("base")).to(JdbcItemRepository.class).asEagerSingleton();
        bind(WorldItemRepository.class).to(PermLazyItemRepository.class).asEagerSingleton();

        expose(WorldItemFactory.class);
        expose(WorldItemRepository.class);

        // items might be externally loaded
        bind(ITEM_TABLE).annotatedWith(Names.named("backdoor")).to(ITEM_TABLE);
        expose(ITEM_TABLE).annotatedWith(Names.named("backdoor"));
    }

    @SuppressWarnings("unchecked")
    @Provides
    NetworkComponentFactory<ObjectEffect> provideObjectEffectFactory() {
        return (NetworkComponentFactory) ProtocolTypeManager.createNewManager();
    }

    @Provides
    Worker provideItemRepositoryWorker(ExecutorService executor) {
        return Workers.wrap(executor);
    }
}
