package org.heat.world.players.chat;

import org.heat.world.chat.PrivateChannelMessage;
import org.heat.world.chat.WorldChannel;
import org.heat.world.chat.WorldChannelLookup;
import org.heat.world.chat.WorldChannelMessage;
import org.heat.world.players.Player;
import org.heat.world.players.PlayerRegistry;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class VirtualPrivateChannelLookup implements WorldChannelLookup {
    private final Supplier<Player> player;
    private final PlayerRegistry playerRegistry;

    public VirtualPrivateChannelLookup(Supplier<Player> player, PlayerRegistry playerRegistry) {
        this.player = player;
        this.playerRegistry = playerRegistry;
    }

    @Override
    public WorldChannel lookupChannel(WorldChannelMessage o) {
        if (o instanceof PrivateChannelMessage) {
            PrivateChannelMessage message = (PrivateChannelMessage) o;

            if (message instanceof PrivateChannelMessage.ByReceiverId) {
                PrivateChannelMessage.ByReceiverId byId = (PrivateChannelMessage.ByReceiverId) message;
                Player player = playerRegistry.findPlayer(byId.getReceiverId());
                if (player == null) {
                    throw new NoSuchElementException();
                }
                return player;
            } else if (message instanceof PrivateChannelMessage.ByReceiverName) {
                PrivateChannelMessage.ByReceiverName byName = (PrivateChannelMessage.ByReceiverName) message;
                Player player = playerRegistry.findPlayerByName(byName.getReceiverName());
                if (player == null) {
                    throw new NoSuchElementException();
                }
                return player;
            } else if (message instanceof PrivateChannelMessage.Resolved) {
                return message.getReceiver();
            }

            throw new Error("unhandlable private message " + message);
        }

        return null;
    }

    @Override
    public void forEach(Consumer<WorldChannel> fn) {
        fn.accept(player.get());
    }
}
