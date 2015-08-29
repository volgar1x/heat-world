package org.heat.datacenter;

import org.fungsi.concurrent.Future;
import org.rocket.Service;
import org.rocket.ServicePath;

import java.util.Map;
import java.util.Optional;

public interface Datacenter extends Service {
    @Override
    default ServicePath path() {
        return ServicePath.absolute("datacenter");
    }

    @Override
    default ServicePath dependsOn() {
        return ServicePath.absolute("maps");
    }

    <T> Optional<T> find(Class<T> klass, int id);
    <T> Future<Map<Integer, T>> findAll(Class<T> klass);
    <T> int getNrObjects(Class<T> klass);
}
