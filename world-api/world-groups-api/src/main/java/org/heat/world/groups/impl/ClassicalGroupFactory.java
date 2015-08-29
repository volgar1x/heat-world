package org.heat.world.groups.impl;

import com.github.blackrush.acara.EventBus;
import com.typesafe.config.Config;
import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.WorldGroupFactory;
import org.heat.world.groups.WorldGroupMember;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClassicalGroupFactory implements WorldGroupFactory {
    private final Provider<EventBus> eventBusBuilder;
    private final int maxMembers;
    private final AtomicInteger nextId = new AtomicInteger();

    @Inject
    ClassicalGroupFactory(Provider<EventBus> eventBusBuilder, Config config) {
        this.eventBusBuilder = eventBusBuilder;
        this.maxMembers = config.getInt("heat.world.groups.max-members");
    }

    @Override
    public WorldGroup create(WorldGroupMember leader) {
        return new ClassicalGroup(nextId.incrementAndGet(), eventBusBuilder.get(), maxMembers, leader);
    }
}
