package org.heat.login.backend;

import com.ankamagames.dofus.network.enums.ServerStatusEnum;
import com.ankamagames.dofus.network.types.connection.GameServerInformations;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.fungsi.Either;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Promise;
import org.fungsi.concurrent.Promises;
import org.heat.User;
import org.heat.backend.messages.AuthorizeUserNotif;
import org.heat.backend.messages.GetNrPlayersNotif;
import org.heat.shared.MoreFutures;
import org.rocket.network.NetworkClient;

import java.util.Map;

@Slf4j
public class DefaultBackend implements Backend {

    final BackendSupervisor supervisor;
    final NetworkClient client;

    final Map<Integer, Promise<String>> pendingAuthorizations = Maps.newConcurrentMap();
    final Map<Integer, Integer> nrPlayers = Maps.newConcurrentMap();
    final Map<Integer, Promise<Integer>> nrPlayersPromises = Maps.newConcurrentMap();
    int id;
    boolean authenticated, joinable;
    ServerStatusEnum status = ServerStatusEnum.STATUS_UNKNOWN;
    String publicAddress = "";
    int publicPort;
    byte completion;
    double date;

    public DefaultBackend(BackendSupervisor supervisor, NetworkClient client) {
        this.supervisor = supervisor;
        this.client = client;
    }

    private Future<DefaultBackend> selfFuture() {
        if (authenticated) {
            return Futures.success(this);
        }
        return Futures.failure(new IllegalStateException());
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ServerStatusEnum getStatus() {
        if (!authenticated) {
            throw new IllegalStateException();
        }
        return this.status;
    }

    @Override
    public String getPublicAddress() {
        if (!authenticated || !joinable) {
            throw new IllegalStateException();
        }
        return this.publicAddress;
    }

    @Override
    public int getPublicPort() {
        if (!authenticated || !joinable) {
            throw new IllegalStateException();
        }
        return this.publicPort;
    }

    @Override
    public Future<GameServerInformations> toGameServerInformations(User user) {
        if (!authenticated || !joinable) {
            throw new IllegalStateException();
        }
        return getNrPlayers(user)
            .map(nrPlayers -> new GameServerInformations(
                    // TODO(login/backend): a server might not be accessible by a given user
                    id, status.value, completion, true, nrPlayers.byteValue(), date
            ))
            ;
    }

    @Override
    public Future<String> authorizeUser(User user) {
        return selfFuture()
                .flatMap(x -> client.write(new AuthorizeUserNotif(user.getId(), user.getUpdatedAt())))
                .flatMap(u -> {
                    Promise<String> authorizationResult = Promises.create();
                    pendingAuthorizations.put(user.getId(), authorizationResult);
                    return authorizationResult;
                })
                ;
    }

    @Override
    public Future<Integer> getNrPlayers(User user) {
        return selfFuture()
                .flatMap(self -> {
                    Integer nrPlayers = self.nrPlayers.get(user.getId());
                    if (nrPlayers != null) {
                        return Futures.success(nrPlayers);
                    }

                    Promise<Integer> promise = Promises.create();
                    nrPlayersPromises.put(user.getId(), promise);
                    return client.write(new GetNrPlayersNotif(user.getId()))
                            .flatMap(x -> promise);
                });
    }

    @Override
    public Future<Unit> authenticate(int id) {
        this.id = id;
        return Futures.success(this)
                .flatMap(MoreFutures.wrap(supervisor::authenticate))
                .onSuccess(u -> {
                    this.authenticated = true;
                });
    }

    @Override
    public Future<Unit> release() {
        return selfFuture()
                .flatMap(MoreFutures.wrap(supervisor::release))
                .onSuccess(u -> {
                    this.authenticated = false;
                });
    }

    @Override
    public void pushDetails(String publicAddress, int publicPort, byte completion, double date) {
        if (!authenticated) return;

        this.publicAddress = publicAddress;
        this.publicPort = publicPort;
        this.completion = completion;
        this.date = date;
        this.joinable = true;

        supervisor.getEventBus().publish(new NewBackendEvent(this));
    }

    @Override
    public void pushNewStatus(ServerStatusEnum newStatus) {
        if (!authenticated) return;

        this.status = newStatus;

        if (joinable) {
            supervisor.getEventBus().publish(new StatusBackendEvent(this));
        }
    }

    @Override
    public void pushNrPlayers(int userId, int nrPlayers) {
        if (!authenticated) return;

        this.nrPlayers.put(userId, nrPlayers);

        Promise<Integer> promise = nrPlayersPromises.get(userId);
        if (promise != null) {
            promise.complete(nrPlayers);
        }
    }

    @Override
    public void pushUserAuthorization(int userId, Either<String, Throwable> ticket) {
        if (!authenticated) return;

        Promise<String> authorizationResult = pendingAuthorizations.get(userId);

        if (authorizationResult == null) {
            log.warn("{} has not requested an authorization", userId);
            return;
        }

        authorizationResult.set(ticket);
    }
}
