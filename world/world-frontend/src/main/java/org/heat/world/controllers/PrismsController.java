package org.heat.world.controllers;

import com.ankamagames.dofus.network.messages.game.prism.PrismsListMessage;
import com.ankamagames.dofus.network.messages.game.prism.PrismsListRegisterMessage;
import org.heat.world.controllers.utils.RolePlaying;
import org.rocket.network.Controller;
import org.rocket.network.NetworkClient;
import org.rocket.network.Receive;

import javax.inject.Inject;
import java.util.stream.Stream;

@Controller
@RolePlaying
public class PrismsController {
    @Inject NetworkClient client;

    // TODO(world/frontend): prisms

    @Receive
    public void getPrisms(PrismsListRegisterMessage msg) {
        client.write(new PrismsListMessage(Stream.empty()));
    }
}
