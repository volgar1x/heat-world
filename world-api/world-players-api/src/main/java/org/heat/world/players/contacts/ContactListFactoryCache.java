package org.heat.world.players.contacts;

import org.fungsi.concurrent.Future;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class ContactListFactoryCache implements ContactListFactory {
    private final ContactListFactory factory;
    private final Map<Integer, Future<? extends ContactList>> cache =
            new ConcurrentHashMap<>();

    public ContactListFactoryCache(ContactListFactory factory) {
        this.factory = factory;
    }

    @Override
    public Future<? extends ContactList> build(int userId) {
        return cache.computeIfAbsent(userId, x -> factory.build(userId)
                                    .onSuccess(y -> cache.remove(userId)));
    }
}
