package org.heat.world;

import com.github.blackrush.acara.EventBus;
import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import org.heat.UserRank;
import org.heat.world.chat.DedicatedWorldChannel;
import org.heat.world.chat.SharedChannelLookup;
import org.heat.world.chat.WorldChannelLookup;
import org.heat.world.controllers.*;
import org.heat.world.groups.WorldGroup;
import org.heat.world.groups.chat.GroupChannelLookup;
import org.heat.world.guilds.chat.GuildChannelLookup;
import org.heat.world.players.Player;
import org.heat.world.players.PlayerRegistry;
import org.heat.world.players.chat.CurrentMapChannelLookup;
import org.heat.world.players.chat.VirtualPrivateChannelLookup;
import org.heat.world.roleplay.WorldAction;
import org.heat.world.users.WorldUser;
import org.rocket.network.Prop;
import org.rocket.network.guice.ControllerModule;

import javax.inject.Provider;
import java.time.Clock;

import static com.ankamagames.dofus.network.enums.ChatChannelsMultiEnum.*;

public class StdFrontendControllerModule extends ControllerModule {
    @Override
    protected void configure() {
        newController(UsersController.class);
        newController(PlayersController.class);
        newController(FriendsController.class);
        newController(PrismsController.class);
        newController(RolePlayController.class);
        newController(SecurityController.class);
        newController(NotificationsController.class);
        newController(ItemsController.class);
        newController(ShortcutsController.class);
        newController(PlayerTradesController.class);
        newController(GroupsController.class);
        newController(ChatController.class);
        newController(ContactsController.class);
        newController(BasicsController.class);
        newController(GuildsController.class);

        newProp(WorldUser.class);
        newProp(Player.class);
        newProp(WorldAction.class);
        newProp(WorldGroup.class, Names.named("main"));
    }

    @Provides
    @Singleton
    SharedChannelLookup provideSharedChannelLookup(Provider<EventBus> eventBusProvider, Clock clock, Config config) {
        UserRank minLevel = UserRank.valueOf(config.getString("heat.world.chat-admin-min-level"));

        return new SharedChannelLookup(ImmutableList.of(
                new DedicatedWorldChannel(CHANNEL_SALES.value, eventBusProvider.get(), clock),
                new DedicatedWorldChannel(CHANNEL_SEEK.value, eventBusProvider.get(), clock),
                new DedicatedWorldChannel(CHANNEL_ADMIN.value, eventBusProvider.get(), clock,
                                          (ch, spkr) -> spkr.getSpeakerRank().enough(minLevel))));
    }

    @Provides
    VirtualPrivateChannelLookup provideVirtualPrivateChannelLookup(Prop<Player> player, PlayerRegistry playerRegistry) {
        return new VirtualPrivateChannelLookup(player::get, playerRegistry);
    }

    @Provides
    CurrentMapChannelLookup provideCurrentMapChannelLookup(Prop<Player> player) {
        return new CurrentMapChannelLookup(player::get);
    }

    @Provides
    GroupChannelLookup provideGroupChannelLookup(@Named("main") Prop<WorldGroup> mainGroup) {
        return new GroupChannelLookup(mainGroup::orEmpty);
    }

    @Provides
    GuildChannelLookup provideGuildChannelLookup(Prop<Player> player) {
        return new GuildChannelLookup(() -> player.isDefined() ? player.get().getGuild()
                                                               : null);
    }

    @Provides
    WorldChannelLookup provideChannelLookup(
            SharedChannelLookup shared,
            VirtualPrivateChannelLookup virtualPrivate,
            CurrentMapChannelLookup currentMap,
            GroupChannelLookup group,
            GuildChannelLookup guild
    ) {
        return currentMap
            .andThen(guild)
            .andThen(group)
            .andThen(virtualPrivate)
            .andThen(shared);
    }
}
