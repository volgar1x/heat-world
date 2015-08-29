package org.heat.world.players.contacts;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public final class ContactsModule extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    ContactListFactory provideContactListRepository(ContactListFactoryImpl factory) {
        return new ContactListFactoryCache(factory);
    }
}
