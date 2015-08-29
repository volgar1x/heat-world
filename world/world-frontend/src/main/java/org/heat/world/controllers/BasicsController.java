package org.heat.world.controllers;

import com.ankamagames.dofus.network.messages.common.basic.BasicPingMessage;
import com.ankamagames.dofus.network.messages.common.basic.BasicPongMessage;
import org.rocket.network.Controller;
import org.rocket.network.NetworkClient;
import org.rocket.network.Receive;

import javax.inject.Inject;

@Controller
public class BasicsController {
    @Inject NetworkClient client;

    @Receive
    public void ping(BasicPingMessage msg) {
        client.write(new BasicPongMessage(msg.quiet));
    }
}
