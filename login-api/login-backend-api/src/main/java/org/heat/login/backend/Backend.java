package org.heat.login.backend;

import com.ankamagames.dofus.network.enums.ServerStatusEnum;
import com.ankamagames.dofus.network.types.connection.GameServerInformations;
import org.fungsi.Either;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.heat.User;

public interface Backend {
    int getId();
    ServerStatusEnum getStatus();
    String getPublicAddress();
    int getPublicPort();

    Future<Unit> authenticate(int id);
    Future<Unit> release();

    Future<String> authorizeUser(User user);
    Future<Integer> getNrPlayers(User user);
    void pushDetails(String publicAddress, int publicPort, byte completion, double date);
    void pushNewStatus(ServerStatusEnum newStatus);
    void pushNrPlayers(int userId, int nrPlayers);
    void pushUserAuthorization(int userId, Either<String, Throwable> ticket);

    Future<GameServerInformations> toGameServerInformations(User user);
}
