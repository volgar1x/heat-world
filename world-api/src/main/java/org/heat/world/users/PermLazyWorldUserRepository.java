package org.heat.world.users;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public final class PermLazyWorldUserRepository implements WorldUserRepository, WorldUserRepository.Unsafe {
    private final WorldUserRepository repository;

    private final Map<Integer, WorldUser> cacheById = new ConcurrentHashMap<>();
    private final Map<String, WorldUser> cacheByNickname = new ConcurrentHashMap<>();

    @Inject
    public PermLazyWorldUserRepository(@Named("base") WorldUserRepository repository) {
        this.repository = repository;
    }

    private void store(WorldUser user) {
        cacheById.put(user.getId(), user);
        cacheByNickname.put(user.getNickname(), user);
    }

    private void storeAll(List<WorldUser> users) {
        for (WorldUser user : users) {
            store(user);
        }
    }

    @Override
    public Future<WorldUser> find(int id) {
        WorldUser user = cacheById.get(id);
        if (user != null) {
            return Futures.success(user);
        }

        return repository.find(id).onSuccess(this::store);
    }

    @Override
    public Future<WorldUser> findOrRefresh(int id, Instant updatedAt) {
        WorldUser user = cacheById.get(id);
        if (user != null && user.getUpdatedAt().compareTo(updatedAt) >= 0) {
            return Futures.success(user);
        }

        return repository.find(id).onSuccess(this::store);
    }

    @Override
    public Future<List<WorldUser>> findMany(int[] ids) {
        List<WorldUser> users = new LinkedList<>();
        for (int i = 0; i < ids.length; i++) {
            WorldUser user = cacheById.get(ids[i]);
            if (user != null) {
                users.add(user);
                ids[i] = 0;
            }
        }

        if (users.size() == ids.length) {
            return Futures.success(users);
        }

        int[] newIds = new int[ids.length - users.size()];
        for (int i = 0, j = 0; i < ids.length; i++) if (ids[i] != 0) newIds[j++] = ids[i];

        return repository.findMany(newIds)
                .map(list -> {
                    storeAll(list);
                    users.addAll(list);
                    return users;
                });
    }

    @Override
    public Future<WorldUser> findByNickname(String nickname) {
        WorldUser user = cacheByNickname.get(nickname);
        if (user != null) {
            return Futures.success(user);
        }

        return repository.findByNickname(nickname).onSuccess(this::store);
    }

    @Override
    public Future<Unit> save(WorldUser user) {
        if (cacheById.containsKey(user.getId())) {
            repository.getUnsafe().update(user);
            return Futures.unit();
        }

        return isPresent(user)
            .flatMap(x -> x != null && x
                ? repository.getUnsafe().insert(user)
                : repository.getUnsafe().update(user))
            .toUnit();
    }

    @Override
    public Unsafe getUnsafe() {
        return this;
    }

    @Override
    public Future<Boolean> isPresent(WorldUser user) {
        if (cacheById.containsKey(user.getId())) {
            return Futures.success(Boolean.TRUE);
        }

        return repository.getUnsafe().isPresent(user).rescue(x -> Boolean.FALSE);
    }

    @Override
    public Future<WorldUser> insert(WorldUser user) {
        cacheById.put(user.getId(), user);
        return repository.getUnsafe().insert(user);
    }

    @Override
    public Future<WorldUser> update(WorldUser user) {
        return repository.getUnsafe().update(user);
    }
}
