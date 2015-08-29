package org.heat.login;

import org.heat.login.controllers.FrontendController;
import org.rocket.network.guice.ControllerModule;

public class StdFrontendControllerModule extends ControllerModule {
    @Override
    protected void configure() {
        newController().to(FrontendController.class);
    }
}
