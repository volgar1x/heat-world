package org.heat.datacenter;

import com.google.common.collect.Maps;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.dofus.d2p.maps.DofusMap;
import org.rocket.StartReason;

import java.util.Map;

public final class PermLazyMapDatacenter<T extends DofusMap> implements MapDatacenter<T> {

    private final MapDatacenter<T> datacenter;
    private final Map<Long, T> cache = Maps.newConcurrentMap();

    public PermLazyMapDatacenter(MapDatacenter<T> datacenter) {
        this.datacenter = datacenter;
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
    public Future<T> fetch(long id) {
        T map = cache.get(id);
        if (map != null) {
            return Futures.success(map);
        }

        return datacenter.fetch(id)
                .onSuccess(x -> cache.put(x.getId(), x))
                ;
    }
}
