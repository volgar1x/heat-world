package org.heat.world.backend;

import org.heat.User;
import org.heat.world.users.UserRepository;

public interface BackendUserRepository extends UserRepository {
    void push(User user);
}
