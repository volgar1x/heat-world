package org.heat.world.players.contacts;

import com.ankamagames.dofus.network.types.game.friend.FriendInformations;
import com.ankamagames.dofus.network.types.game.friend.IgnoredInformations;
import org.heat.world.users.WorldUser;
import org.rocket.Nullable;

import java.util.stream.Stream;

public interface ContactList {
    void add(WorldUser friend);
    @Nullable WorldUser deleteById(int friendUserId);

    void ignore(WorldUser ignored);
    @Nullable WorldUser unignore(int ignoredUserId);

    boolean isFriendWith(int userId);
    default boolean isFriendWith(WorldUser user) {return isFriendWith(user.getId());}

    Stream<WorldUser> getFriendStream();

    Stream<FriendInformations> toFriendInformations();
    Stream<IgnoredInformations> toIgnoredInformations();
}
