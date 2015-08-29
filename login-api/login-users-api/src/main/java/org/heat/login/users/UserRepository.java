package org.heat.login.users;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.heat.User;

public interface UserRepository {
    Future<User> find(int id);
    Future<User> findByUsername(String username);

    Future<User> save(User user);

    Future<Unit> removeCurrentWorldId(int worldId);
}
