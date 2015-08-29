package org.heat.world.players.contacts;

import lombok.extern.slf4j.Slf4j;
import org.fungsi.concurrent.Future;
import org.heat.shared.MoreFutures;
import org.heat.world.players.PlayerRegistry;
import org.heat.world.users.WorldUser;
import org.heat.world.users.WorldUserRepository;

import javax.inject.Inject;

import java.util.List;

import static org.heat.world.players.contacts.ContactRepository.Kind.FRIEND;
import static org.heat.world.players.contacts.ContactRepository.Kind.IGNORED;

@Slf4j
final class ContactListFactoryImpl implements ContactListFactory {
    private WorldUserRepository users;
    private PlayerRegistry players;
    private ContactRepository repository;

    @Inject
    ContactListFactoryImpl(WorldUserRepository users, PlayerRegistry players, ContactRepository repository) {
        this.users = users;
        this.players = players;
        this.repository = repository;
    }

    @Override
    public Future<? extends ContactList> build(int userId) {
        log.debug("Build contact list of {}", userId);

        // TODO load friend and ignore list in two request
        // it is for now using
        //  - 1 SQL request for N friends
        //  - 1 SQL request for M ignored
        //  - N backend request for N friends
        //  - M backend request for M ignored
        // ideally it would be better to make only one SQL request
        // and only one backend request
        Future<List<WorldUser>> friends = repository.getLinksFrom(userId, FRIEND).flatMap(users::findMany);
        Future<List<WorldUser>> ignored = repository.getLinksFrom(userId, IGNORED).flatMap(users::findMany);

        return MoreFutures.join(friends, ignored)
                .map(pair -> {
                    List<WorldUser> f = pair.first, i = pair.second;
                    return new ContactListImpl(players, repository, userId, f, i);
                });
    }
}
