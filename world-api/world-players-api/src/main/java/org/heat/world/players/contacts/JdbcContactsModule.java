package org.heat.world.players.contacts;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class JdbcContactsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ContactRepository.class).to(JdbcContactRepository.class).in(Scopes.SINGLETON);
    }
}
