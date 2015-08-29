package org.heat.world.players;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface PlayerRepository {
    Future<AtomicInteger> createIdGenerator();

    Future<Player> find(int id);
    Future<List<Player>> findByUserId(int userId);
    Future<Player> findByName(String name);

    Future<Unit> create(Player player);
    Future<Unit> save(Player player);
    Future<Unit> remove(Player player);
}
