package org.heat.world;

import com.github.blackrush.acara.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.typesafe.config.Config;
import io.netty.buffer.ByteBufAllocator;
import org.fungsi.concurrent.Workers;
import org.heat.datacenter.*;
import org.heat.dofus.d2o.D2oReader;
import org.heat.dofus.d2o.HeatDataClassLookup;
import org.heat.dofus.d2p.DlmReader;
import org.heat.shared.database.Repository;
import org.heat.world.roleplay.environment.WorldMap;
import org.heat.world.roleplay.environment.WorldMapRepository;
import org.heat.world.roleplay.environment.WorldMapRepositoryImpl;

import javax.inject.Provider;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class StdDatacenterModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<Repository<WorldMap>>(){}).to(WorldMapRepository.class);
        bind(new TypeLiteral<MapDatacenter<WorldMap>>(){}).to(WorldMapRepository.class);
    }

    @Provides
    D2oReader provideD2oReader(ByteBufAllocator alloc) {
        return new D2oReader(alloc, HeatDataClassLookup.INSTANCE);
    }

    @Provides
    @Singleton
    Datacenter provideDatacenter(Config config) {
        return new JaSerFileDatacenter(Paths.get(config.getString("heat.world.datacenter-path")));
    }

    @Provides
    @Singleton
    WorldMapRepository provideMapDatacenter(ExecutorService executor, Config config, Provider<EventBus> eventBusProvider) {
        Path dataPath = Paths.get(config.getString("heat.world.maps.data-path"));
        Duration loadTimeout = Duration.ofMillis(config.getDuration("heat.world.maps.load-timeout", TimeUnit.MILLISECONDS));

        Path pakPath = dataPath.resolve(config.getString("heat.world.maps.origin"));
        Supplier<WorldMap> mapSupplier = () -> new WorldMap(eventBusProvider.get());
        byte[] key = DlmReader.parseKey(config.getString("heat.world.maps.key"));

        MapDatacenter<WorldMap> datacenter;
        datacenter = new PakMapDatacenter<>(pakPath, mapSupplier, key, Workers.wrap(executor));
        datacenter = new PermLazyMapDatacenter<>(datacenter);
        return new WorldMapRepositoryImpl(datacenter, loadTimeout);
    }
}
