package org.heat.login;

import org.heat.login.controllers.BackendController;
import org.rocket.network.guice.ControllerModule;

public class StdBackendControllerModule extends ControllerModule {
    @Override
    protected void configure() {
        newController().to(BackendController.class);
    }
}
