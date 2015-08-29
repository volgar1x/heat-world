package org.heat.world;

import com.google.inject.AbstractModule;
import org.heat.world.roleplay.environment.WorldPositioningSystem;
import org.heat.world.roleplay.environment.WorldPositioningSystemImpl;

public class StdWorldEnvironmentModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WorldPositioningSystem.class).to(WorldPositioningSystemImpl.class).asEagerSingleton();
    }
}
