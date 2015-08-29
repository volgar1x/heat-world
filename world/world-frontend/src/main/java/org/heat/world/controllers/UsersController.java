package org.heat.world.controllers;

import com.ankamagames.dofus.datacenter.breeds.Breed;
import com.ankamagames.dofus.network.messages.game.approach.*;
import lombok.extern.slf4j.Slf4j;
import org.heat.world.backend.Backend;
import org.heat.world.controllers.utils.Basics;
import org.heat.world.users.UserCapabilities;
import org.heat.world.users.WorldUser;
import org.heat.world.users.WorldUserRepository;
import org.rocket.network.*;
import org.rocket.network.props.PropPresence;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

@Controller
@Slf4j
public class UsersController {
    @Inject NetworkClient client;
    @Inject Backend backend;
    @Inject MutProp<WorldUser> user;
    @Inject UserCapabilities capabilities;
    @Inject WorldUserRepository repository;

    @Connect
    public void onConnect() {
        client.write(HelloGameMessage.i);
    }

    private short exportBreeds(List<Breed> breeds) {
        short res = 0;
        for (Breed breed : breeds) {
            res = (short) (res | 1 << (breed.getId() - 1));
        }
        return res;
    }

    private short exportVisibleBreeds() {
        return exportBreeds(capabilities.getVisibleBreeds(user.get().getUser()));
    }

    private short exportAvailableBreeds() {
        return exportBreeds(capabilities.getAvailableBreeds(user.get().getUser()));
    }

    @PropPresence(value = WorldUser.class, presence = false)
    @Receive
    public void authenticate(AuthenticationTicketMessage msg) {
        backend.authenticateUser(msg.ticket)
                .onSuccess(user::set)
                .onFailure(err -> log.error("cannot authenticate a user", err))
                .flatMap(user -> {
                    user.setLastConnection(Instant.now());
                    repository.save(user);

                    return client.transaction(tx -> {
                        tx.write(AuthenticationTicketAcceptedMessage.i);
                        tx.write(Basics.time());
                        // TODO(world/frontend): server lang, community and type
                        tx.write(new ServerSettingsMessage("fr", (byte) 0, (byte) 0));
                        tx.write(new AccountCapabilitiesMessage(
                                user.getId(),
                                capabilities.isTutorialAvailable(user.getUser()),
                                exportVisibleBreeds(),
                                exportAvailableBreeds(),
                                (byte) 0 // TODO(world/frontend): status when authenticating
                        ));
                    });
                })
                .mayRescue(cause -> client.write(AuthenticationTicketRefusedMessage.i).flatMap(x -> client.close()))
        ;
    }

    @Disconnect
    public void acknowledgeUserDisconnection() {
        if (user.isDefined()) {
            backend.acknowledgeDisconnection(user.get().getId());
        }
    }
}
