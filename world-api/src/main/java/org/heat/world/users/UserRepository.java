package org.heat.world.users;

import org.fungsi.concurrent.Future;
import org.heat.User;

import java.time.Instant;

public interface UserRepository {
    Future<User> find(int id);
    Future<User> findOrRefresh(int id, Instant updatedAt);
}
