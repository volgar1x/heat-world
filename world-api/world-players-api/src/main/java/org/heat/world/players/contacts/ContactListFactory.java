package org.heat.world.players.contacts;

import org.fungsi.concurrent.Future;

public interface ContactListFactory {
    Future<? extends ContactList> build(int userId);
}
