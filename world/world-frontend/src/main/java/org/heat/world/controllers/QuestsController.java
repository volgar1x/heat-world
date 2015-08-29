package org.heat.world.controllers;

import com.ankamagames.dofus.network.messages.game.context.roleplay.quest.QuestListMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.quest.QuestListRequestMessage;
import org.heat.world.controllers.utils.RolePlaying;
import org.rocket.network.Controller;
import org.rocket.network.NetworkClient;
import org.rocket.network.Receive;

import javax.inject.Inject;
import java.util.stream.Stream;

@Controller
@RolePlaying
public class QuestsController {
    @Inject NetworkClient client;

    // TODO(world/frontend): quests

    @Receive
    public void getQuestList(QuestListRequestMessage msg) {
        client.write(new QuestListMessage(new short[0], new short[0], Stream.empty()));
    }
}
