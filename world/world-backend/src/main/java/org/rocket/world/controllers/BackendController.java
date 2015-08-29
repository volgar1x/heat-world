package org.rocket.world.controllers;

import org.heat.backend.messages.*;
import org.heat.world.backend.Backend;
import org.heat.world.backend.BackendUserRepository;
import org.heat.world.players.Player;
import org.heat.world.players.PlayerRepository;
import org.rocket.InjectConfig;
import org.rocket.network.Controller;
import org.rocket.network.Disconnect;
import org.rocket.network.NetworkClient;
import org.rocket.network.Receive;

import javax.inject.Inject;
import java.util.List;

@Controller
public class BackendController {
    @Inject NetworkClient client;
    @Inject Backend backend;
    @Inject PlayerRepository players;
    @Inject BackendUserRepository userRepository;

    @InjectConfig("heat.world.id") int worldId;
    @InjectConfig("heat.world.frontend.public-host") String publicHost;
    @InjectConfig("heat.world.frontend.port") int port;

    @Disconnect
    public void onDisconnect() {

    }

    @Receive
    public void handshake(HandshakeNotif notif) {
        client.write(new IdentificationReq(worldId));
    }

    @Receive
    public void identification(IdentificationResp resp) {
        if (!resp.success) {
            throw new Error("backend was not able to identicate");
        }

        client.transaction(tx -> {
            tx.write(new SetStatusReq(backend.getCurrentStatus()));
            tx.write(new SetDetailsReq(publicHost, port, (byte) 0, 0.0));
        });
    }

    @Receive
    public void authorizeUser(AuthorizeUserNotif notif) {
        backend.authorizeUser(notif)
            .flatMap(ticket -> client.write(new AuthorizeUserReq(notif.userId, ticket)))
            .mayRescue(cause -> client.write(new ForbidUserReq(notif.userId, cause)));
    }

    @Receive
    public void getNrPlayers(GetNrPlayersNotif notif) {
        List<Player> players = this.players.findByUserId(notif.userId).get(); // TODO(world/backend): player load timeout
        client.write(new SetNrPlayersReq(notif.userId, players.size()));
    }

    @Receive
    public void getUser(GetUserResp resp) {
        userRepository.push(resp.user);
    }
}
