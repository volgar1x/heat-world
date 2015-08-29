package org.heat.world.backend;

import com.ankamagames.dofus.network.enums.ServerStatusEnum;
import com.google.common.collect.Maps;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Timer;
import org.heat.backend.messages.AckUserAuthReq;
import org.heat.backend.messages.AuthorizeUserNotif;
import org.heat.backend.messages.SetNrPlayersReq;
import org.heat.backend.messages.SetStatusReq;
import org.heat.shared.Strings;
import org.heat.world.users.WorldUser;
import org.heat.world.users.WorldUserRepository;
import org.rocket.network.NetworkClient;
import org.rocket.network.NetworkClientService;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class DefaultBackend implements Backend {

    private final NetworkClient client;
    private final WorldUserRepository userRepository;
    private final Random random;
    private final Timer userAuthTtl;
    private final Duration userAuthTtlDuration;
    private final Map<String, AuthorizeUserNotif> users = Maps.newConcurrentMap();

    private ServerStatusEnum status = ServerStatusEnum.ONLINE;

    @Inject
    public DefaultBackend(
            @Named("backend") NetworkClientService client,
            @Named("ticket") Random random,
            Timer userAuthTtl,
            Config config,
            WorldUserRepository userRepository
    ) {
        this.client = client;
        this.random = random;
        this.userAuthTtl = userAuthTtl;
        this.userRepository = userRepository;
        this.userAuthTtlDuration = Duration.ofNanos(config.getDuration("heat.world.backend.user-auth-ttl", TimeUnit.NANOSECONDS));
    }

    @Override
    public ServerStatusEnum getCurrentStatus() {
        return status;
    }

    @Override
    public Future<String> authorizeUser(AuthorizeUserNotif notif) {
        String ticket = Strings.randomString(random, 64);
        users.put(ticket, notif);
        userAuthTtl.schedule(userAuthTtlDuration, () -> {
            if (users.remove(ticket, notif)) {
                client.write(new AckUserAuthReq(notif.userId, false));
            }
        });
        return Futures.success(ticket);
    }

    @Override
    public Future<WorldUser> authenticateUser(String ticket) {
        AuthorizeUserNotif notif = users.remove(ticket);

        if (notif == null) {
            return Futures.failure(new NoSuchElementException());
        }

        client.write(new AckUserAuthReq(notif.userId, true));
        return userRepository.findOrRefresh(notif.userId, notif.updatedAt);
    }

    @Override
    public void setNewStatus(ServerStatusEnum newStatus) {
        client.write(new SetStatusReq(newStatus));
    }

    @Override
    public void setNrPlayers(int userId, int nrPlayers) {
        client.write(new SetNrPlayersReq(userId, nrPlayers));
    }

    @Override
    public void acknowledgeDisconnection(int userId) {
        client.write(new AckUserAuthReq(userId, false));
    }
}
