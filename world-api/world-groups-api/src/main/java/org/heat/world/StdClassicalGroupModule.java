package org.heat.world;

import com.google.inject.AbstractModule;
import org.heat.world.groups.WorldGroupFactory;
import org.heat.world.groups.impl.ClassicalGroupFactory;

public class StdClassicalGroupModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WorldGroupFactory.class).to(ClassicalGroupFactory.class).asEagerSingleton();
    }
}
