package org.heat.world.backend;

import com.ankamagames.dofus.network.enums.ServerStatusEnum;
import org.fungsi.concurrent.Future;
import org.heat.User;
import org.heat.backend.messages.AuthorizeUserNotif;
import org.heat.world.users.WorldUser;

public interface Backend {
    ServerStatusEnum getCurrentStatus();

    Future<String> authorizeUser(AuthorizeUserNotif notif);
    Future<WorldUser> authenticateUser(String ticket);

    void setNewStatus(ServerStatusEnum newStatus);
    void setNrPlayers(int userId, int nrPlayers);
    void acknowledgeDisconnection(int userId);

    default void acknowledgeDisconnection(User user) {
        acknowledgeDisconnection(user.getId());
    }
}
