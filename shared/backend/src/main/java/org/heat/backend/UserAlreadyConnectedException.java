package org.heat.backend;

public class UserAlreadyConnectedException extends RuntimeException {
    public UserAlreadyConnectedException() {
        super("user is already connected");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
