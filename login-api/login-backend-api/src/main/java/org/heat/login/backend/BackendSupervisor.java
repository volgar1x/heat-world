package org.heat.login.backend;

import com.github.blackrush.acara.EventBus;
import org.rocket.Service;

import java.util.Optional;
import java.util.stream.Stream;

public interface BackendSupervisor extends Service {
    EventBus getEventBus();

    void authenticate(Backend backend);
    void release(Backend backend);

    Stream<Backend> getBackendStream();

    default Optional<Backend> find(int id) {
        return getBackendStream().filter(x -> x.getId() == id).findAny();
    }

    default Backend get(int id) {
        return find(id).get();
    }
}
