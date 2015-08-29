package org.heat.world.users;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;

import java.time.Instant;
import java.util.List;

public interface WorldUserRepository {
    Future<WorldUser> find(int id);
    Future<WorldUser> findOrRefresh(int id, Instant updatedAt);
    Future<List<WorldUser>> findMany(int[] ids);
    Future<WorldUser> findByNickname(String nickname);

    Future<Unit> save(WorldUser user);

    @Deprecated
    Unsafe getUnsafe();

    interface Unsafe {
        Future<Boolean> isPresent(WorldUser user);
        Future<WorldUser> insert(WorldUser user);
        Future<WorldUser> update(WorldUser user);
    }
}
