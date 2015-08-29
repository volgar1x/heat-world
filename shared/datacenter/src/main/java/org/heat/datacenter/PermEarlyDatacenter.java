package org.heat.datacenter;

import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.rocket.StartReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public abstract class PermEarlyDatacenter implements Datacenter {
    public static final Logger log = LoggerFactory.getLogger(PermEarlyDatacenter.class);

    private Map<Class<?>, Map<Integer, Object>> cache;

    @Override
    public void start(StartReason reason) {
        if (cache != null) {
            throw new IllegalStateException();
        }

        log.debug("loading...");
        cache = load();
        log.info("loaded!");
    }

    @Override
    public void stop() {
        cache = null;
    }

    protected abstract Map<Class<?>, Map<Integer, Object>> load();

    @Override
    public <T> Optional<T> find(Class<T> klass, int id) {
        if (cache == null) {
            throw new IllegalStateException();
        }

        Map<Integer, Object> subcache = cache.get(klass);
        if (subcache == null) {
            return Optional.empty();
        }

        Object val = subcache.get(id);
        if (val == null) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked") T tmp = (T) val;
        return Optional.of(tmp);
    }

    @Override
    public <T> Future<Map<Integer, T>> findAll(Class<T> klass) {
        if (cache == null) {
            throw new IllegalStateException();
        }

        Map<Integer, Object> subcache = cache.get(klass);
        if (subcache == null) {
            return Future.never();
        }

        @SuppressWarnings("unchecked") Map<Integer, T> tmp = (Map) subcache;
        return Futures.success(tmp);
    }

    @Override
    public <T> int getNrObjects(Class<T> klass) {
        if (cache == null) {
            throw new IllegalStateException();
        }

        Map<Integer, Object> subcache = cache.get(klass);
        return subcache != null ? subcache.size() : 0;
    }
}
