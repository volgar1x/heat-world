package org.heat.login.controllers;

import com.ankamagames.dofus.network.NetworkMetadata;
import com.ankamagames.dofus.network.enums.IdentificationFailureReasonEnum;
import com.ankamagames.dofus.network.messages.connection.*;
import com.ankamagames.dofus.network.messages.handshake.ProtocolRequired;
import com.github.blackrush.acara.Listen;
import com.github.blackrush.acara.Subscription;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.User;
import org.heat.UserRank;
import org.heat.login.backend.*;
import org.heat.login.frontend.ClientAuthenticationMessage;
import org.heat.login.users.UserAuthenticationException;
import org.heat.login.users.UserRepository;
import org.heat.login.users.UserSecurity;
import org.heat.shared.MoreFutures;
import org.rocket.network.*;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;

@Controller
public class FrontendController {
    public static final ProtocolRequired PROTOCOL_REQUIRED = new ProtocolRequired(NetworkMetadata.REQUIRED_BUILD, NetworkMetadata.CURRENT_BUILD);

    @Inject NetworkClient client;
    @Inject BackendSupervisor backends;
    @Inject UserSecurity userSecurity;
    @Inject UserRepository users;

    Subscription backendSubscription;

    String salt;
    User user;

    private Future<Unit> writeServerList() {
        return backends.getBackendStream()
                .map(backend -> backend.toGameServerInformations(user))
                .collect(MoreFutures.collect())
                .map(Collection::stream)
                .map(ServersListMessage::new)
                .flatMap(client::write)
                ;
    }

    @Connect
    public void onConnect() {
        salt = userSecurity.getNewSalt();

        client.transaction(tx -> {
            tx.write(PROTOCOL_REQUIRED);
            tx.write(new HelloConnectMessage(salt, userSecurity.getPublicKey()));
        });
    }

    @Disconnect
    public void onDisconnect() {
        if (backendSubscription != null) {
            backendSubscription.revoke();
        }

        if (user != null) {
            user.setConnected(false);
            users.save(user);
        }
    }

    @Receive
    public void identify(ClientAuthenticationMessage msg) {
        userSecurity.authenticate(msg.username, msg.credentials)
            .onSuccess(user -> user.setConnected(true))
            .flatMap(users::save)
            .flatMap(user -> {
                this.user = user;

                client.write(new IdentificationSuccessMessage(
                        user.getRank().enough(UserRank.ANNOUNCER),
                        false,
                        user.getUsername(),
                        user.getNickname(),
                        user.getId(),
                        user.getCommunityId(),
                        user.getSecretQuestion(),
                        user.getSubscriptionEndMilliOrZero(),
                        user.getCreatedAt().toEpochMilli()
                ));

                if (msg.autoconnect) {
                    if (msg.serverId != 0) {
                        return selectServer(msg.serverId);
                    } else if (user.getLastServerId().isPresent()) {
                        Optional<Backend> opt = backends.find(user.getLastServerId().getAsInt());
                        if (opt.isPresent()) {
                            return selectServer(opt.get());
                        }
                    }
                }

                backendSubscription = backends.getEventBus().subscribe(this);
                return writeServerList();
            })
            .mayRescue(cause -> {
                if (cause instanceof UserAuthenticationException) {
                    UserAuthenticationException ex = (UserAuthenticationException) cause;

                    if (ex.getReason() == IdentificationFailureReasonEnum.BAD_VERSION) {
                        return client.write(new IdentificationFailedForBadVersionMessage(
                                IdentificationFailureReasonEnum.BAD_VERSION.value,
                                ex.getRequiredVersion().get()
                        )).flatMap(x -> client.close());
                    } else if (ex.getReason() == IdentificationFailureReasonEnum.BANNED) {
                        return client.write(new IdentificationFailedBannedMessage(
                                IdentificationFailureReasonEnum.BANNED.value,
                                ex.getBanEndMilliOrZero()
                        )).flatMap(x -> client.close());
                    } else {
                        return client.write(new IdentificationFailedMessage(ex.getReason().value)).flatMap(x -> client.close());
                    }
                }

                return Futures.failure(cause);
            })
            .onFailure(x -> client.close())
            .onFailure(Throwable::printStackTrace)
            ;
    }

    @Receive
    public void select(ServerSelectionMessage msg) {
        selectServer(msg.serverId);
    }

    private Future<Unit> selectServer(int id) {
        return selectServer(backends.get(id));
    }

    private Future<Unit> selectServer(Backend backend) {
        return backend.authorizeUser(user)
            // NOTE(Blackrush): the user will be saved later, when his connection is closed
            .onSuccess(x -> {
                user.setLastServerId(OptionalInt.of(backend.getId()));
                user.setCurrentWorldId(OptionalInt.of(backend.getId()));
            })

            .flatMap(ticket -> client.write(new SelectedServerDataMessage(
                    (short) backend.getId(),
                    backend.getPublicAddress(),
                    backend.getPublicPort(),
                    true,
                    ticket
            )))
            .mayRescue(cause -> {
                if (cause instanceof UserAuthorizationException) {
                    UserAuthorizationException ex = (UserAuthorizationException) cause;

                    return client.write(new SelectedServerRefusedMessage(
                            (short) backend.getId(),
                            ex.getError().value,
                            backend.getStatus().value
                    ));
                }

                return Futures.unit();
            })
            .flatMap(x -> client.close())
            ;
    }

    @Listen
    public void backendUpdate(StatusBackendEvent evt) {
        evt.getBackend().toGameServerInformations(user)
            .map(ServerStatusUpdateMessage::new)
            .flatMap(client::write)
            ;
    }

    @Listen
    public void newBackend(NewBackendEvent evt) {
        writeServerList();
    }

}
