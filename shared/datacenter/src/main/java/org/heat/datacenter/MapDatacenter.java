package org.heat.datacenter;

import org.fungsi.concurrent.Future;
import org.heat.dofus.d2p.maps.DofusMap;
import org.rocket.Service;
import org.rocket.ServicePath;
import org.rocket.StartReason;

public interface MapDatacenter<T extends DofusMap> extends Service {
    Future<T> fetch(long id);

    @Override
    default ServicePath path() {
        return ServicePath.absolute("maps");
    }

    @Override
    default ServicePath dependsOn() {
        return null;
    }

    @Override
    default void start(StartReason reason) {}

    @Override
    default void stop() {}
}
