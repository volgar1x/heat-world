package org.heat.login.backend;

import com.github.blackrush.acara.EventBus;
import org.rocket.ServicePath;
import org.rocket.StartReason;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DefaultBackendSupervisor implements BackendSupervisor {

    private final EventBus eventBus;
    private final Map<Integer, Backend> backends = new HashMap<>();

    public DefaultBackendSupervisor(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public ServicePath path() {
        return ServicePath.of(this);
    }

    @Override
    public ServicePath dependsOn() {
        return null;
    }

    @Override
    public void start(StartReason reason) {

    }

    @Override
    public void stop() {

    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Stream<Backend> getBackendStream() {
        return backends.values().stream();
    }

    @Override
    public Optional<Backend> find(int id) {
        return Optional.ofNullable(backends.get(id));
    }

    @Override
    public void authenticate(Backend backend) {
        if (backends.containsKey(backend.getId())) {
            throw new IllegalArgumentException();
        }

        backends.put(backend.getId(), backend);
    }

    @Override
    public void release(Backend backend) {
        if (!backends.remove(backend.getId(), backend)) {
            throw new IllegalArgumentException();
        }
    }
}
