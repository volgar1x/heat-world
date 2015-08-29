package org.heat.world.players.contacts;

import com.ankamagames.dofus.network.types.game.friend.FriendInformations;
import com.ankamagames.dofus.network.types.game.friend.IgnoredInformations;
import org.heat.world.players.Player;
import org.heat.world.players.PlayerRegistry;
import org.heat.world.users.WorldUser;
import org.rocket.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.heat.world.players.contacts.ContactRepository.Kind.FRIEND;
import static org.heat.world.players.contacts.ContactRepository.Kind.IGNORED;

final class ContactListImpl implements ContactList {

    private final PlayerRegistry players;
    private final ContactRepository repository;
    private final int ownerId;
    private final List<WorldUser> friends, ignoredList;

    ContactListImpl(PlayerRegistry players, ContactRepository repository, int ownerId, List<WorldUser> friends, List<WorldUser> ignoredList) {
        this.players = players;
        this.repository = repository;
        this.ownerId = ownerId;
        this.friends = friends;
        this.ignoredList = ignoredList;
    }

    @Override
    public void add(WorldUser friend) {
        friends.add(friend);
        repository.link(ownerId, friend.getId(), FRIEND);
    }

    @Override
    public @Nullable WorldUser deleteById(int friendUserId) {
        Iterator<WorldUser> it = friends.iterator();
        while (it.hasNext()) {
            WorldUser friend = it.next();
            if (friend.getId() == friendUserId) {
                it.remove();
                repository.unlink(ownerId, friendUserId);
                return friend;
            }
        }
        return null;
    }

    @Override
    public void ignore(WorldUser ignored) {
        ignoredList.add(ignored);
        repository.link(ownerId, ignored.getId(), IGNORED);
    }

    @Override
    public @Nullable WorldUser unignore(int ignoredUserId) {
        Iterator<WorldUser> it = ignoredList.iterator();
        while (it.hasNext()) {
            WorldUser ignored = it.next();
            if (ignored.getId() == ignoredUserId) {
                it.remove();
                repository.unlink(ownerId, ignoredUserId);
                return ignored;
            }
        }
        return null;
    }

    @Override
    public boolean isFriendWith(int userId) {
        for (WorldUser friend : friends) {
            if (friend.getId() == userId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Stream<WorldUser> getFriendStream() {
        return friends.stream();
    }

    @Override
    public Stream<FriendInformations> toFriendInformations() {
        return friends.stream().map(friend -> {
            Player player = players.findPlayerByUser(friend);
            if (player != null) {
                return player.toFriendInformations();
            }
            return friend.toFriendInformations();
        });
    }

    @Override
    public Stream<IgnoredInformations> toIgnoredInformations() {
        return Stream.empty();
    }
}
