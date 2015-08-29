package org.heat.login.backend;

import com.ankamagames.dofus.network.enums.ServerConnectionErrorEnum;

public class UserAuthorizationException extends RuntimeException {
    private final ServerConnectionErrorEnum error;

    public UserAuthorizationException(ServerConnectionErrorEnum error) {
        super("user was not authorized because " + error);
        this.error = error;
    }

    public ServerConnectionErrorEnum getError() {
        return error;
    }
}
