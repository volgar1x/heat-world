package org.heat.world;

import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.rocket.dist.Rocket;
import org.rocket.guice.ConfigModule;

import java.io.File;

public class StdWorldRocket extends Rocket {
    @Override
    public Config getConfig() {
        return ConfigFactory.parseFileAnySyntax(new File("world.conf")).resolve();
    }

    @Override
    public Module getModule() {
        return binder -> {
            binder.bind(Config.class).toInstance(getConfig());
            binder.install(ConfigModule.of(getConfig()));
            binder.install(new StdWorldModule());
        };
    }
}
