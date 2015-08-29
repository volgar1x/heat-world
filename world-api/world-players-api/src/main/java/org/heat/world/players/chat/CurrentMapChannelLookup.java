package org.heat.world.players.chat;

import org.heat.world.chat.MapChannel;
import org.heat.world.chat.WorldChannel;
import org.heat.world.chat.WorldChannelLookup;
import org.heat.world.chat.WorldChannelMessage;
import org.heat.world.players.Player;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.ankamagames.dofus.network.enums.ChatChannelsMultiEnum.CHANNEL_GLOBAL;

public final class CurrentMapChannelLookup implements WorldChannelLookup {
    private final Supplier<Player> player;

    public CurrentMapChannelLookup(Supplier<Player> player) {
        this.player = player;
    }

    @Override
    public WorldChannel lookupChannel(WorldChannelMessage message) {
        if (message.getChannelId() == CHANNEL_GLOBAL.value) {
            return new MapChannel(player.get().getPosition().getMap());
        }

        return null;
    }

    @Override
    public void forEach(Consumer<WorldChannel> fn) {
        fn.accept(new MapChannel(player.get().getPosition().getMap()));
    }
}
