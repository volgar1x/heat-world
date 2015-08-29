package org.heat.login.controllers;

import org.fungsi.Either;
import org.heat.backend.messages.*;
import org.heat.login.backend.Backend;
import org.heat.login.users.UserRepository;
import org.rocket.network.*;

import javax.inject.Inject;
import java.util.OptionalInt;

@Controller
public class BackendController {

    @Inject NetworkClient client;
    @Inject Backend backend;
    @Inject UserRepository userRepository;

    @Connect
    public void onConnect() {
        client.write(new HandshakeNotif());
    }

    @Disconnect
    public void onDisconnect() {
        userRepository.removeCurrentWorldId(backend.getId());
        backend.release();
    }

    @Receive
    public void identification(IdentificationReq req) {
        backend.authenticate(req.id)
                .flatMap(u -> client.write(new IdentificationResp(true)))
                .mayRescue(cause -> client.write(new IdentificationResp(false)))
                ;
    }

    @Receive
    public void setStatus(SetStatusReq req) {
        backend.pushNewStatus(req.newStatus);
    }

    @Receive
    public void setNrPlayers(SetNrPlayersReq req) {
        backend.pushNrPlayers(req.userId, req.nrPlayers);
    }

    @Receive
    public void authorizeUser(AuthorizeUserReq req) {
        backend.pushUserAuthorization(req.userId, Either.success(req.ticket));
    }

    @Receive
    public void forbidUser(ForbidUserReq req) {
        backend.pushUserAuthorization(req.userId, Either.failure(req.cause));
    }

    @Receive
    public void setDetails(SetDetailsReq req) {
        backend.pushDetails(req.publicAddress, req.publicPort, req.completion, req.date);
    }

    @Receive
    public void ackUserAuth(AckUserAuthReq req) {
        userRepository.find(req.userId)
            .flatMap(user -> {
                user.setCurrentWorldId(
                    req.success
                        ? OptionalInt.of(backend.getId())
                        : OptionalInt.empty());

                return userRepository.save(user);
            });
    }

    @Receive
    public void getUser(GetUserReq req) {
        userRepository.find(req.userId)
            .flatMap(user ->
                client.write(new GetUserResp(user))
            );
    }
}
