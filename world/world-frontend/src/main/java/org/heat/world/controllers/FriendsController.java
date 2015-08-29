package org.heat.world.controllers;

import com.ankamagames.dofus.network.messages.game.friend.*;
import com.github.blackrush.acara.Listen;
import com.github.blackrush.acara.Subscription;
import org.fungsi.concurrent.Future;
import org.heat.world.controllers.events.CreateContextEvent;
import org.heat.world.controllers.events.DestroyContextEvent;
import org.heat.world.controllers.events.roleplay.NewFriendEvent;
import org.heat.world.controllers.utils.Authenticated;
import org.heat.world.players.Player;
import org.heat.world.players.PlayerRepository;
import org.heat.world.players.contacts.ContactList;
import org.heat.world.users.WorldUser;
import org.heat.world.users.WorldUserRepository;
import org.rocket.network.Controller;
import org.rocket.network.NetworkClient;
import org.rocket.network.Prop;
import org.rocket.network.Receive;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Controller
@Authenticated
public class FriendsController {
    @Inject NetworkClient client;
    @Inject Prop<Player> player;
    @Inject Prop<WorldUser> user;

    @Inject PlayerRepository players;
    @Inject WorldUserRepository users;

    Map<Integer, Subscription> friendSubs = new HashMap<>();

    // TODO(world/frontend): friends, ignored, spouse

    private void startListeningToFriends() {
        player.get().getContacts().getFriendStream()
              .forEach(friend -> {
                  Subscription sub = friend.getEventBus().subscribe(this);
                  friendSubs.put(friend.getId(), sub);
              });
    }

    private void stopListeningToFriends() {
        friendSubs.values().forEach(Subscription::revoke);
        friendSubs.clear();
    }

    private Future<WorldUser> findPlayerOrUser(String name) {
        return players.findByName(name)
                      .map(Player::getUser)
                      .mayRescue(err -> users.findByNickname(name));
    }

    @Listen
    public void onCreateContext(CreateContextEvent evt) {
        Player player = this.player.get();

        if (player.getUser().isListeningFriends()) {
            startListeningToFriends();
        }
    }

    @Listen
    public void onDestroyContext(DestroyContextEvent evt) {
        stopListeningToFriends();
    }

    @Receive
    public void getFriendsList(FriendsGetListMessage msg) {
        Player player = this.player.get();
        client.write(new FriendsListMessage(player.getContacts().toFriendInformations()));
    }

    @Receive
    public void addFriend(FriendAddRequestMessage msg) {
        Player player = this.player.get();
        ContactList contacts = player.getContacts();

        findPlayerOrUser(msg.name)
                .flatMap(user -> client.getEventBus().publish(new NewFriendEvent(player, user)).map(o -> user))
                .onFailure(err -> client.write(new FriendAddFailureMessage()))
                .onSuccess(friend -> {
                    contacts.add(friend);
                    client.write(new FriendAddedMessage(friend.toFriendInformations()));
                });
    }

    @Receive
    public void deleteFriend(FriendDeleteRequestMessage msg) {
        Player player = this.player.get();
        ContactList contacts = player.getContacts();

        WorldUser removed = contacts.deleteById(msg.accountId);

        Subscription sub = friendSubs.remove(msg.accountId);
        if (sub != null) {
            sub.revoke();
        }

        client.write(new FriendDeleteResultMessage(removed != null, removed != null ? removed.getNickname() : ""));
    }

    @Receive
    public void setWarnOnConnection(FriendSetWarnOnConnectionMessage msg) {
        WorldUser user = this.user.get();
        user.setListeningFriends(msg.enable);
        users.save(user);

        if (msg.enable) {
            startListeningToFriends();
        } else {
            stopListeningToFriends();
        }
    }

    @Receive
    public void getIgnoredList(IgnoredGetListMessage msg) {
        Player player = this.player.get();
        client.write(new IgnoredListMessage(player.getContacts().toIgnoredInformations()));
    }

    @Receive
    public void ignore(IgnoredAddRequestMessage msg) {
        Player player = this.player.get();
        ContactList contacts = player.getContacts();

        findPlayerOrUser(msg.name)
                .onFailure(err -> client.write(new IgnoredAddFailureMessage()))
                .onSuccess(ignored -> {
                    contacts.ignore(ignored);
                    client.write(new IgnoredAddedMessage(ignored.toIgnoredInformations(),
                                                         msg.session));
                });
    }

    @Receive
    public void unignore(IgnoredDeleteRequestMessage msg) {
        Player player = this.player.get();
        ContactList contacts = player.getContacts();

        WorldUser ignored = contacts.unignore(msg.accountId);
        if (ignored != null) {
            client.write(new IgnoredDeleteResultMessage(true, msg.session, ignored.getNickname()));
        } else {
            client.write(new IgnoredDeleteResultMessage(false, msg.session, ""));
        }
    }

    @Receive
    public void getSpouseInfos(SpouseGetInformationsMessage msg) {
        client.write(new SpouseStatusMessage(false));
    }

    @Listen
    public void onNewFriend(NewFriendEvent evt) {
        if (evt.getCurrentPlayer().getUser().isListeningFriends()) {
            Subscription sub = evt.getNewFriend().getEventBus().subscribe(this);
            friendSubs.put(evt.getNewFriend().getId(), sub);
        }
    }
}
