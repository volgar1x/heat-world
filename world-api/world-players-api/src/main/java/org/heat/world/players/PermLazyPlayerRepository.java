package org.heat.world.players;

import com.google.common.collect.Maps;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class PermLazyPlayerRepository implements PlayerRepository {
    private final PlayerRepository repository;

    private final Map<Integer, Player> cacheById = Maps.newConcurrentMap();
    private final Map<String, Player> cacheByName = Maps.newConcurrentMap();
    private final Map<Integer, List<Player>> cacheByUserId = Maps.newConcurrentMap();
    private final AtomicInteger idGeneratorCache = new AtomicInteger(-1);

    @Inject
    public PermLazyPlayerRepository(@Named("base") PlayerRepository repository) {
        this.repository = repository;
    }

    private void storePlayer(Player player) {
        cacheById.put(player.getId(), player);
        cacheByName.put(player.getName(), player);

        List<Player> players = cacheByUserId.computeIfAbsent(player.getUserId(),
                id -> new LinkedList<>());
        players.add(player);
    }

    private void storePlayers(List<Player> players) {
        players.forEach(this::storePlayer);
    }

    private void discardPlayer(Player player) {
        cacheById.remove(player.getId());
        cacheByName.remove(player.getName());

        List<Player> players = cacheByUserId.get(player.getUserId());
        if (players != null) {
            players.remove(player);
        }
    }

    @Override
    public Future<AtomicInteger> createIdGenerator() {
        if (idGeneratorCache.get() != -1) {
            return Futures.success(idGeneratorCache);
        }

        return repository.createIdGenerator()
                .map(gen -> {
                    idGeneratorCache.set(gen.get());
                    return idGeneratorCache;
                });
    }

    @Override
    public Future<Player> find(int id) {
        Player player = cacheById.get(id);
        if (player != null) {
            return Futures.success(player);
        }

        return repository.find(id)
                .onSuccess(this::storePlayer)
                ;
    }

    @Override
    public Future<List<Player>> findByUserId(int userId) {
        List<Player> players = cacheByUserId.get(userId);
        if (players != null) {
            return Futures.success(Collections.unmodifiableList(players));
        }

        return repository.findByUserId(userId)
                .onSuccess(this::storePlayers)
                ;
    }

    @Override
    public Future<Player> findByName(String name) {
        Player player = cacheByName.get(name);
        if (player != null) {
            return Futures.success(player);
        }

        return repository.findByName(name)
                .onSuccess(this::storePlayer)
                ;
    }

    @Override
    public Future<Unit> create(Player player) {
        storePlayer(player);
        return repository.create(player);
    }

    @Override
    public Future<Unit> save(Player player) {
        return repository.save(player);
    }

    @Override
    public Future<Unit> remove(Player player) {
        discardPlayer(player);
        return repository.remove(player);
    }
}
