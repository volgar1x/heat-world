package org.heat.world.controllers;

import com.ankamagames.dofus.network.messages.security.ClientKeyMessage;
import org.rocket.network.Controller;
import org.rocket.network.NetworkClient;
import org.rocket.network.Receive;

import javax.inject.Inject;

@Controller
public class SecurityController {
    @Inject NetworkClient client;

    @Receive
    public void setClientKey(ClientKeyMessage msg) {
        // TODO(world/frontend): client key
    }
}
