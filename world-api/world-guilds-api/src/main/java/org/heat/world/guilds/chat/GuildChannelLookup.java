package org.heat.world.guilds.chat;

import org.heat.world.chat.WorldChannel;
import org.heat.world.chat.WorldChannelLookup;
import org.heat.world.chat.WorldChannelMessage;
import org.heat.world.guilds.WorldGuild;
import org.rocket.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.ankamagames.dofus.network.enums.ChatChannelsMultiEnum.CHANNEL_GUILD;

public class GuildChannelLookup implements WorldChannelLookup {
    private final Supplier<@Nullable WorldGuild> guildSupplier;

    public GuildChannelLookup(Supplier<@Nullable WorldGuild> guildSupplier) {this.guildSupplier = guildSupplier;}

    @Override
    public WorldChannel lookupChannel(WorldChannelMessage message) {
        if (message.getChannelId() == CHANNEL_GUILD.value) {
            return guildSupplier.get();
        }
        return null;
    }

    @Override
    public void forEach(Consumer<WorldChannel> fn) {
        WorldGuild guild = guildSupplier.get();
        if (guild != null) {
            fn.accept(guild);
        }
    }
}
