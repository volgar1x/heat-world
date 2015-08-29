package org.heat.login.backend;

public abstract class BackendEvent {
    private final Backend backend;

    public BackendEvent(Backend backend) {
        this.backend = backend;
    }

    public Backend getBackend() {
        return backend;
    }
}
