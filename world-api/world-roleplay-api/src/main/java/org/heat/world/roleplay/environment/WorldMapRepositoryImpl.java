package org.heat.world.roleplay.environment;

import org.fungsi.concurrent.Future;
import org.heat.datacenter.MapDatacenter;
import org.rocket.StartReason;

import java.time.Duration;
import java.util.Optional;

public final class WorldMapRepositoryImpl implements WorldMapRepository {
    private final MapDatacenter<WorldMap> datacenter;
    private final Duration loadTimeout;

    public WorldMapRepositoryImpl(MapDatacenter<WorldMap> datacenter, Duration loadTimeout) {
        this.datacenter = datacenter;
        this.loadTimeout = loadTimeout;
    }

    @Override
    public void start(StartReason reason) {
        datacenter.start(reason);
    }

    @Override
    public void stop() {
        datacenter.stop();
    }

    @Override
    public Future<WorldMap> fetch(long id) {
        return datacenter.fetch(id);
    }

    @Override
    public Optional<WorldMap> find(long id) {
        return Optional.of(fetch(id).get(loadTimeout));
    }
}
