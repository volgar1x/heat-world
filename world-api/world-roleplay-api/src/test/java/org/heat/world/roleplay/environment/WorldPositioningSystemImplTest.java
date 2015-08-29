package org.heat.world.roleplay.environment;

import com.ankamagames.dofus.network.enums.DirectionsEnum;
import com.github.blackrush.acara.EventBus;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.fungsi.concurrent.Workers;
import org.heat.datacenter.Datacenter;
import org.heat.datacenter.JaSerFileDatacenter;
import org.heat.datacenter.PakMapDatacenter;
import org.heat.dofus.d2p.DlmReader;
import org.heat.shared.IntPair;
import org.heat.world.StdWorldEnvironmentModule;
import org.junit.Before;
import org.junit.Test;
import org.rocket.StartReason;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorldPositioningSystemImplTest {

    @Inject WorldMapRepository maps;
    @Inject Datacenter datacenter;
    @Inject WorldPositioningSystemImpl wps;

    @Before
    public void setUp() throws Exception {
        File configFile = new File("world.conf").getAbsoluteFile();

        if (!configFile.exists()) {
            throw new FileNotFoundException(configFile.toString());
        }

        Config config = ConfigFactory.parseFileAnySyntax(configFile).resolve();

        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @SuppressWarnings("unchecked")
                    @Override
                    protected void configure() {
                        install(new StdWorldEnvironmentModule());

                        bind(Config.class).toInstance(config);
                    }

                    @Provides
                    @Singleton
                    Datacenter provideDatacenter() {
                        return new JaSerFileDatacenter(Paths.get(config.getString("heat.world.datacenter-path")));
                    }

                    @Provides
                    @Singleton
                    WorldMapRepository provideMapDatacenter() throws IOException {
                        return new WorldMapRepositoryImpl(new PakMapDatacenter<>(
                                Paths.get(config.getString("heat.world.maps.data-path"))
                                    .resolve(config.getString("heat.world.maps.origin")),
                                () -> new WorldMap(mock(EventBus.class)),
                                DlmReader.parseKey(config.getString("heat.world.maps.key")),
                                Workers.wrap(MoreExecutors.directExecutor())
                        ),      Duration.ofMillis(config.getDuration("heat.world.maps.load-timeout", TimeUnit.MILLISECONDS)));
                    }
                }
        );
        injector.injectMembers(this);

        maps.start(StartReason.NORMAL);
        datacenter.start(StartReason.NORMAL);
        wps.start(StartReason.NORMAL);
    }

    @Test
    public void testLocate() throws Exception {
        // given
        int map = 16395;
        WorldMapPoint mapPoint = WorldMapPoint.of(355).get();
        DirectionsEnum direction = DirectionsEnum.DIRECTION_SOUTH_EAST;

        // when
        FullWorldPosition pos = (FullWorldPosition) wps.locate(map, mapPoint, direction);

        // then
        assertNotNull("position", pos);
        assertEquals("position map id", map, pos.getMapId());
        assertEquals("position mapPoint", mapPoint, pos.getMapPoint());
        assertEquals("position direction", direction, pos.getDirection());
        assertEquals("position coordinates", IntPair.of(32, 11), pos.getMapCoordinates());
    }
}
