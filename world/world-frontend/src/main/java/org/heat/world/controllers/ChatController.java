package org.heat.world.controllers;

import com.ankamagames.dofus.network.messages.game.chat.*;
import com.ankamagames.dofus.network.messages.game.chat.channel.EnabledChannelsMessage;
import com.github.blackrush.acara.Listen;
import com.github.blackrush.acara.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.heat.shared.stream.ImmutableCollectors;
import org.heat.world.chat.*;
import org.heat.world.commands.CommandManager;
import org.heat.world.controllers.events.CreateContextEvent;
import org.heat.world.controllers.events.EnterContextEvent;
import org.heat.world.controllers.events.QuitContextEvent;
import org.heat.world.controllers.events.roleplay.chat.NewChannelEvent;
import org.heat.world.controllers.events.roleplay.chat.QuitChannelEvent;
import org.heat.world.controllers.utils.Basics;
import org.heat.world.controllers.utils.RolePlaying;
import org.heat.world.items.WorldItem;
import org.heat.world.players.Player;
import org.heat.world.users.WorldUser;
import org.rocket.network.Controller;
import org.rocket.network.NetworkClient;
import org.rocket.network.Prop;
import org.rocket.network.Receive;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.ankamagames.dofus.network.enums.ChatActivableChannelsEnum.PSEUDO_CHANNEL_PRIVATE;

@Controller
@RolePlaying
@Slf4j
public class ChatController {
    @Inject NetworkClient client;
    @Inject Prop<Player> player;
    @Inject Prop<WorldUser> user;

    @Inject WorldChannelLookup channelLookup;
    @Inject CommandManager commandManager;

    Map<Integer, Subscription> channelSubs = new HashMap<>();

    private void doSpeak(WorldChannelMessage message) {
        if (!channelSubs.containsKey(message.getChannelId())) {
            client.write(Basics.noop());
            return;
        }

        WorldChannel channel = channelLookup.lookupChannel(message);

        if (channel != null) {
            channel.speak(player.get(), message);
        } else {
            log.debug("cannot speak on channel {}", message.getChannelId());
            client.write(Basics.noop());
        }
    }

    private void subscribeAllChannels() {
        Player player  = this.player.get();
        WorldUser user = this.user.get();

        channelLookup.forEach(channel -> {
            if (user.hasChannel(channel.getChannelId()) && channel.accepts(player)) {
                Subscription sub = channel.getSubscribableChannelView().subscribe(this);
                channelSubs.put(channel.getChannelId(), sub);
            }
        });
    }

    private void unsubscribeAllChannels() {
        channelSubs.forEach((cid, sub) -> sub.revoke());
        channelSubs.clear();
    }

    @Listen
    public void sendEnabledChannels(CreateContextEvent evt) {
        Player player = this.player.get();
        WorldUser user = player.getUser();

        client.write(new EnabledChannelsMessage(user.getChannelsAsBytes(),
                                                user.getDisabledChannelsAsBytes()));
    }

    @Listen
    public void subscribeChannels(EnterContextEvent evt) {
        subscribeAllChannels();
    }

    @Listen
    public void unsubscribeChannels(QuitContextEvent evt) {
        unsubscribeAllChannels();
    }

    @Receive
    public void speak(ChatClientMultiMessage msg) {
        Player player = this.player.get();

        if(commandManager.execute(player, msg.content, false)) return;

        WorldChannelMessage message = new StringChannelMessage(msg.channel, msg.content);

        doSpeak(message);
    }

    @Receive
    public void speakWithAttachments(ChatClientMultiWithObjectMessage msg) {
        Player player = this.player.get();

        WorldChannelMessage message = new ChannelMessageWithAttachments(
            new StringChannelMessage(msg.channel, msg.content),
            Stream.of(msg.objects)
                .map(item -> player.getWallet().findByUid(item.objectUID).get())
                .collect(ImmutableCollectors.toList()));

        doSpeak(message);
    }

    @Receive
    public void privatelySpeak(ChatClientPrivateMessage msg) {
        WorldChannelMessage message = new PrivateChannelMessage.ByReceiverName(
                msg.receiver,
                new StringChannelMessage(PSEUDO_CHANNEL_PRIVATE.value, msg.content));

        doSpeak(message);
    }

    @Receive
    public void privatelySpeakWithAttachments(ChatClientPrivateWithObjectMessage msg) {
        Player player = this.player.get();

        WorldChannelMessage message = new PrivateChannelMessage.ByReceiverName(
            msg.receiver,
            new ChannelMessageWithAttachments(
                new StringChannelMessage(PSEUDO_CHANNEL_PRIVATE.value, msg.content),
                Stream.of(msg.objects)
                    .map(item -> player.getWallet().findByUid(item.objectUID).get())
                    .collect(ImmutableCollectors.toList())));

        doSpeak(message);
    }

    @Listen
    public void onEnvelope(WorldChannelEnvelope envelope) {
        Player player = this.player.get();

        WorldSpeaker speaker = envelope.getSpeaker();

        if (envelope.getMessage() instanceof PrivateChannelMessage && speaker == player) {
            PrivateChannelMessage privateMessage = (PrivateChannelMessage) envelope.getMessage();
            WorldMessageReceiver receiver = privateMessage.getReceiver();
            WorldChannelMessage message = privateMessage.getMessage();

            if (message instanceof ChannelMessageWithAttachments) {
                client.write(new ChatServerCopyWithObjectMessage(
                        PSEUDO_CHANNEL_PRIVATE.value,
                        message.getString(),
                        (int) envelope.getInstant().getEpochSecond(),
                        "",
                        receiver.getSpeakerId(),
                        receiver.getSpeakerName(),
                        ((ChannelMessageWithAttachments) message).getAttachments().stream()
                            .map(WorldItem::toObjectItem)
                ));
            } else {
                client.write(new ChatServerCopyMessage(
                        PSEUDO_CHANNEL_PRIVATE.value,
                        message.getString(),
                        (int) envelope.getInstant().getEpochSecond(),
                        "",
                        receiver.getSpeakerId(),
                        receiver.getSpeakerName()
                ));
            }
        } else {
            WorldChannelMessage message = envelope.getMessage();

            if (message instanceof ChannelMessageWithAttachments) {
                client.write(new ChatServerWithObjectMessage(
                        (byte) message.getChannelId(),
                        message.getString(),
                        (int) envelope.getInstant().getEpochSecond(),
                        "",
                        speaker.getSpeakerId(),
                        speaker.getSpeakerName(),
                        speaker.getSpeakerUserId(),
                        ((ChannelMessageWithAttachments) message).getAttachments().stream()
                            .map(WorldItem::toObjectItem)
                ));
            } else {
                client.write(new ChatServerMessage(
                        (byte) message.getChannelId(),
                        message.getString(),
                        (int) envelope.getInstant().getEpochSecond(),
                        "",
                        speaker.getSpeakerId(),
                        speaker.getSpeakerName(),
                        speaker.getSpeakerUserId()
                ));
            }
        }
    }

    @Listen
    public void onNewChannel(NewChannelEvent evt) {
        WorldUser user = this.user.get();
        Player player  = this.player.get();

        WorldChannel channel = evt.getChannel();
        if (user.hasChannel(channel.getChannelId()) && channel.accepts(player)) {
            Subscription sub = channel.getSubscribableChannelView().subscribe(this);
            channelSubs.put(channel.getChannelId(), sub);
        }
    }

    @Listen
    public void onQuitChannel(QuitChannelEvent evt) {
        Subscription sub = channelSubs.remove(evt.getChannel().getChannelId());
        if (sub != null) {
            sub.revoke();
        }
    }
}
