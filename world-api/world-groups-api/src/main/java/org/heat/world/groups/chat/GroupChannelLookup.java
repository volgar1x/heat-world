package org.heat.world.groups.chat;

import org.heat.world.chat.WorldChannel;
import org.heat.world.chat.WorldChannelLookup;
import org.heat.world.chat.WorldChannelMessage;
import org.heat.world.groups.WorldGroup;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.ankamagames.dofus.network.enums.ChatChannelsMultiEnum.CHANNEL_PARTY;

public final class GroupChannelLookup implements WorldChannelLookup {
    private final Supplier<Optional<WorldGroup>> group;

    public GroupChannelLookup(Supplier<Optional<WorldGroup>> group) {
        this.group = group;
    }

    @Override
    public WorldChannel lookupChannel(WorldChannelMessage message) {
        if (message.getChannelId() == CHANNEL_PARTY.value) {
            Optional<WorldGroup> option = group.get();
            if (option.isPresent()) {
                return option.get();
            }
        }

        return null;
    }

    @Override
    public void forEach(Consumer<WorldChannel> fn) {
        group.get().ifPresent(fn);
    }
}
