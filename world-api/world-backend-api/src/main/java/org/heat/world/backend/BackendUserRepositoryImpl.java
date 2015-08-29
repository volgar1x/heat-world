package org.heat.world.backend;

import org.fungsi.concurrent.*;
import org.heat.User;
import org.heat.backend.messages.GetUserReq;
import org.rocket.network.NetworkClient;
import org.rocket.network.NetworkClientService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BackendUserRepositoryImpl implements BackendUserRepository {
    public static final Duration LOAD_USER_TIMEOUT = Duration.ofMillis(5000);

    private final NetworkClient client;
    private final Timer timer;

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Promise<User>> userPromises = new ConcurrentHashMap<>();

    @Inject
    public BackendUserRepositoryImpl(@Named("backend") NetworkClientService client, Timer timer) {
        this.client = client;
        this.timer = timer;
    }

    private Future<User> load0(int id) {
        Promise<User> promise = userPromises.computeIfAbsent(id, x -> {
            client.write(new GetUserReq(id));
            return Promises.create();
        });

        return promise.within(LOAD_USER_TIMEOUT, timer);
    }

    @Override
    public Future<User> find(int id) {
        User user = users.get(id);
        if (user != null) {
            return Futures.success(user);
        }
        return load0(id);
    }

    @Override
    public Future<User> findOrRefresh(int id, Instant updatedAt) {
        User user = users.get(id);
        if (user != null && updatedAt.compareTo(updatedAt) <= 0) {
            return Futures.success(user);
        }
        return load0(id);
    }

    @Override
    public void push(User user) {
        users.put(user.getId(), user);

        Promise<User> promise = userPromises.remove(user.getId());
        if (promise != null) {
            promise.complete(user);
        }
    }
}
