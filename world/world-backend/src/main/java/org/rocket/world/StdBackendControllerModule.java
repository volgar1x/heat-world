package org.rocket.world;

import org.rocket.network.guice.ControllerModule;
import org.rocket.world.controllers.BackendController;

public class StdBackendControllerModule extends ControllerModule {
    @Override
    protected void configure() {
        newController(BackendController.class);
    }
}
