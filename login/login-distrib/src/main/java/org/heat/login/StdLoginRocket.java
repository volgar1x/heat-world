package org.heat.login;

import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.rocket.dist.Rocket;
import org.rocket.guice.ConfigModule;

import java.io.File;

public class StdLoginRocket extends Rocket {
    @Override
    public Config getConfig() {
        return ConfigFactory.parseFileAnySyntax(new File("login.conf"));
    }

    @Override
    public Module getModule() {
        Config config = getConfig();
        return binder -> {
            binder.bind(Config.class).toInstance(config);
            binder.install(ConfigModule.of(config));
            binder.install(new StdLoginModule());
        };
    }
}
