package org.heat.world.items;

import com.google.common.collect.Maps;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class PermLazyItemRepository implements WorldItemRepository {
    private final WorldItemRepository repository;

    private final Map<Integer, WorldItem> cache = Maps.newConcurrentMap();

    @Inject
    public PermLazyItemRepository(@Named("base") WorldItemRepository repository) {
        this.repository = repository;
    }

    void store(WorldItem item) {
        cache.put(item.getUid(), item);
    }

    void unstore(WorldItem item) {
        cache.remove(item.getUid());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void transferOwnership(List<WorldItem> items) {
        items.forEach(this::store);
    }

    @Override
    public Future<WorldItem> find(int uid) {
        WorldItem item = cache.get(uid);
        if (item != null) {
            return Futures.success(item);
        }

        return repository.find(uid)
                .onSuccess(this::store)
                ;
    }

    @Override
    public Future<List<WorldItem>> find(IntStream uidStream) {
        List<WorldItem> cached = new LinkedList<>();
        IntStream.Builder notCached = IntStream.builder();

        uidStream.forEach(uid -> {
            WorldItem item = cache.get(uid);
            if (item != null) {
                cached.add(item);
            } else {
                notCached.add(uid);
            }
        });

        return repository.find(notCached.build())
                .map(items -> {
                    items.forEach(this::store);
                    cached.addAll(items);
                    return cached;
                });
    }

    @Override
    public Future<WorldItem> save(WorldItem item) {
        if (item.getUid() != 0) {
            WorldItem cached = cache.get(item.getUid());
            if (cached != null && item.getVersion() < cached.getVersion()) {
                throw new OutdatedItemException(item, cached);
            }
        }

        return repository.save(item).onSuccess(this::store);
    }

    @Override
    public Future<WorldItem> remove(WorldItem item) {
        WorldItem cached = cache.get(item.getUid());
        if (cached != null && item.getVersion() != cached.getVersion()) {
            throw new OutdatedItemException(item, cached);
        }

        cache.remove(item.getUid());
        return repository.remove(item);
    }
}
