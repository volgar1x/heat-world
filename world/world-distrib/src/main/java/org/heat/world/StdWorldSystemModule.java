package org.heat.world;

import com.github.blackrush.acara.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class StdWorldSystemModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventBus.class).annotatedWith(WorldSystemLocal.class).to(EventBus.class).asEagerSingleton();

        Provider<EventBus> sysEventBus = getProvider(Key.get(EventBus.class, WorldSystemLocal.class));
        bindListener(MATCHER, new TypeListener() {
            @Override
            public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                encounter.register((InjectionListener<I>) injectee ->
                        sysEventBus.get().subscribe(injectee));
            }
        });
    }

    private static final Matcher<TypeLiteral<?>> MATCHER = new AbstractMatcher<TypeLiteral<?>>() {
        @Override
        public boolean matches(TypeLiteral<?> typeLiteral) {
            return typeLiteral.getRawType().isAnnotationPresent(WorldSystemListen.class);
        }
    };
}
