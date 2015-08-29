package org.heat.world.controllers;

import com.ankamagames.dofus.network.messages.game.basic.BasicNoOperationMessage;
import com.ankamagames.dofus.network.messages.game.context.notification.NotificationListMessage;
import com.ankamagames.dofus.network.messages.game.context.notification.NotificationResetMessage;
import com.ankamagames.dofus.network.messages.game.context.notification.NotificationUpdateFlagMessage;
import com.github.blackrush.acara.Listen;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.heat.world.controllers.events.ChoosePlayerEvent;
import org.heat.world.controllers.utils.RolePlaying;
import org.heat.world.players.Player;
import org.heat.world.players.notifications.NotificationConverter;
import org.heat.world.players.notifications.PlayerNotifRepository;
import org.rocket.network.Controller;
import org.rocket.network.MutProp;
import org.rocket.network.NetworkClient;
import org.rocket.network.Receive;

import javax.inject.Inject;

@Controller
@RolePlaying
public class NotificationsController {
    @Inject NetworkClient client;
    @Inject MutProp<Player> player;

    @Inject PlayerNotifRepository notifs;

    @Listen
    public Future<Unit> knownNotifications(ChoosePlayerEvent evt) {
        return notifs.findAll(evt.getPlayer().getId()).map(array ->
                        client.write(new NotificationListMessage(
                                        NotificationConverter.compressNotifications(array))
                        )
        ).toUnit();
    }

    @Receive
    public void updateNotificationFlag(NotificationUpdateFlagMessage msg) {
        notifs.save(player.get().getId(), msg.index);
        client.write(BasicNoOperationMessage.i);
    }

    @Receive
    public void clearNotificationsRequested(NotificationResetMessage msg) {
        if(msg.isAlwaysEmpty())
            notifs.removeAll(player.get().getId());
        client.write(BasicNoOperationMessage.i);
    }
}
