package org.heat.world.controllers;

import com.ankamagames.dofus.network.enums.GameContextEnum;
import com.ankamagames.dofus.network.messages.game.basic.BasicNoOperationMessage;
import com.ankamagames.dofus.network.messages.game.guild.*;
import com.github.blackrush.acara.Listen;
import com.github.blackrush.acara.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.heat.world.controllers.events.ChoosePlayerEvent;
import org.heat.world.controllers.events.CreateContextEvent;
import org.heat.world.controllers.utils.Idling;
import org.heat.world.guilds.*;
import org.heat.world.guilds.events.GuildCreationEvent;
import org.heat.world.guilds.events.KickedGuildMemberEvent;
import org.heat.world.players.Player;
import org.heat.world.players.PlayerRegistry;
import org.heat.world.roleplay.WorldAction;
import org.rocket.network.*;

import javax.inject.Inject;
import java.util.NoSuchElementException;

import static com.ankamagames.dofus.network.enums.SocialGroupCreationResultEnum.*;
import static com.ankamagames.dofus.network.enums.SocialGroupInvitationStateEnum.*;
import static com.ankamagames.dofus.network.enums.TextInformationTypeEnum.TEXT_INFORMATION_ERROR;

@Controller
@Slf4j
public class GuildsController {
    @Inject NetworkClient client;
    @Inject Prop<Player> player;
    @Inject MutProp<WorldAction> currentAction;

    @Inject
    WorldGuildRepository guilds;
    @Inject
    WorldGuildMemberRepository guildMembers;
    @Inject
    WorldGuildFactory guildFactory;
    @Inject
    PlayerRegistry registry;

    Subscription playerSub, guildSub;

    @Listen
    public void onPlayerChoice(ChoosePlayerEvent evt) {
        Player player = evt.getPlayer();

        playerSub = player.getEventBus().subscribe(this);

        guilds.findByMember(player)
              .onFailure(err -> {
                  if (!(err instanceof NoSuchElementException)) {
                      log.error("cannot load guild", err);
                  }
              });
    }

    @Listen
    public void onCreateRoleplayContext(CreateContextEvent evt) {
        if (evt.getContext() != GameContextEnum.ROLE_PLAY) {
            return;
        }

        Player player = this.player.get();
        WorldGuild guild = player.getGuild();

        if (guild == null) {
            return;
        }

        guildSub = guild.getEventBus().subscribe(this);

        client.write(new GuildMembershipMessage(guild.toGuildInformations(),
                                                player.getGuildPermissions().getBits(),
                                                true));
    }

    @Disconnect
    public void onDisconnect() {
        if (playerSub != null) {
            playerSub.revoke();
            playerSub = null;
        }
        if (guildSub != null) {
            guildSub.revoke();
            guildSub = null;
        }
    }

    @Listen
    public void onGuildCreation(GuildCreationEvent evt) {
        Player player = this.player.get();
        if (player.getGuild() != null) {
            client.write(BasicNoOperationMessage.i);
        } else {
            client.write(GuildCreationStartedMessage.i);
        }
    }

    @Receive
    public void create(GuildCreationValidMessage msg) {
        Player player = this.player.get();

        try {
            WorldGuild guild = guildFactory.create(player, msg.guildName, msg.guildEmblem);
            player.setGuild(guild);

            guilds.save(guild).flatMap(x -> guildMembers.save(player))
                  .onSuccess(x -> client.transaction(tx -> {
                      tx.write(new GuildCreationResultMessage(SOCIAL_GROUP_CREATE_OK.value));
                      tx.write(new GuildJoinedMessage(guild.toGuildInformations(),
                                                      player.getGuildPermissions().getBits(),
                                                      /*enabled=*/true));
                  }));

        } catch (WorldGuildFactory.InvalidNameErr err) {
            client.write(new GuildCreationResultMessage(SOCIAL_GROUP_CREATE_ERROR_NAME_INVALID.value));
        } catch (WorldGuildFactory.ExistingNameErr err) {
            client.write(new GuildCreationResultMessage(SOCIAL_GROUP_CREATE_ERROR_NAME_ALREADY_EXISTS.value));
        } catch (WorldGuildFactory.InvalidEmblemErr err) {
            client.write(new GuildCreationResultMessage(SOCIAL_GROUP_CREATE_ERROR_EMBLEM_INVALID.value));
        }
    }

    @Receive
    public void infos(GuildGetInformationsMessage msg) {
        Player player = this.player.get();
        WorldGuild guild = player.getGuild();

        switch (msg.infoType) {
            case 1: //INFO_GENERAL
                // TODO GuildsController#infos
                client.write(new GuildInformationsGeneralMessage(
                        true, false,
                        (short) 0, 0.0, 0.0, 0.0, // experience related stats
                        0, // creation date
                        (short) 0, (short) 0 // members related stats
                ));
                break;

            case 2: //INFO_MEMBERS
                client.write(new GuildInformationsMembersMessage(guild.toGuildMember()));
                break;

            case 3: //INFO_BOOSTS

            case 4: //INFO_PADDOCKS

            case 5: //INFO_HOUSES

            case 6: //INFO_TAX_COLLECTOR_GUILD_ONLY

            case 7: //INFO_TAX_COLLECTOR_ALLIANCE

            case 8: //INFO_TAX_COLLECTOR_LEAVE

            default:
                client.write(BasicNoOperationMessage.i);
                break;
        }
    }

    @Idling
    @Receive
    public void invite(GuildInvitationMessage msg) {
        Player     player = this.player.get();
        WorldGuild guild  = player.getGuild();
        Player     target = registry.findPlayer(msg.targetId);

        if (guild == null) {
            client.write(BasicNoOperationMessage.i);
            return;
        }

        if (target == null) {
            client.write(new GuildInvitationStateRecruterMessage("", SOCIAL_GROUP_INVITATION_FAILED.value));
            return;
        }

        WorldGuild.Invitation invit = guild.invite(player, target);

        if (invit == null) {
            player.notice(WorldGuild.I18N.NO_MORE_MEMBERS, TEXT_INFORMATION_ERROR, guild.getNrMembersLimit());
            return;
        }

        this.currentAction.set(invit);

        invit.getEndFuture()
             .onSuccess(action -> client.write(new GuildInvitationStateRecruterMessage(
                     target.getName(), SOCIAL_GROUP_INVITATION_OK.value)))
             .onFailure(err -> client.write(new GuildInvitationStateRecruterMessage(
                     target.getName(), SOCIAL_GROUP_INVITATION_CANCELED.value)))
             .respond(e -> this.currentAction.forget());

        target.getEventBus().publish(invit)
              .onSuccess(ack -> client.write(new GuildInvitationStateRecruterMessage(
                      target.getName(), SOCIAL_GROUP_INVITATION_SENT.value)))
              .onFailure(err -> client.write(new GuildInvitationStateRecruterMessage(
                      target.getName(), SOCIAL_GROUP_INVITATION_FAILED.value)));
    }

    @Listen
    public void onInvited(WorldGuild.Invitation invit) {
        if (this.currentAction.isDefined()) {
            throw new WorldGuild.Invitation.Failure();
        }

        Player player = this.player.get();

        if (player.getGuild() != null) {
            throw new WorldGuild.Invitation.Failure();
        }

        this.currentAction.set(invit);

        invit.getEndFuture()
             .onSuccess(action -> this.onInvitationAccepted(invit))
             .onFailure(err -> client.write(
                     new GuildInvitationStateRecrutedMessage(SOCIAL_GROUP_INVITATION_CANCELED.value)))
             .respond(e -> this.currentAction.forget());

        client.write(new GuildInvitedMessage(invit.getRecruter().getActorId(), invit.getRecruter().getActorName(),
                                             invit.getGuild().toBasicGuildInformations()));
    }

    private void onInvitationAccepted(WorldGuild.Invitation invit) {
        Player player = this.player.get();
        WorldGuild guild = invit.getGuild();

        player.setGuild(guild);

        guildSub = guild.getEventBus().subscribe(this);

        client.transaction(tx -> {
            tx.write(new GuildInvitationStateRecrutedMessage(SOCIAL_GROUP_INVITATION_OK.value));

            tx.write(new GuildJoinedMessage(guild.toGuildInformations(),
                                            player.getGuildPermissions().getBits(),
                                            true));
        });
    }

    @Receive
    public void answerInvitation(GuildInvitationAnswerMessage msg) {
        if (!this.currentAction.isDefined()) {
            client.write(BasicNoOperationMessage.i);
            return;
        }

        WorldAction action = this.currentAction.get();
        if (!(action instanceof WorldGuild.Invitation)) {
            // maybe there are better options
            client.write(BasicNoOperationMessage.i);
            return;
        }

        WorldGuild.Invitation invit = (WorldGuild.Invitation) action;
        Player                player = this.player.get();

        if (invit.getRecruter() == player && msg.accept) {
            // clearly a hack...
            client.write(BasicNoOperationMessage.i);
            return;
        } else if (invit.getRecruted() == player && msg.accept) {
            player.setGuild(invit.getGuild());
        }

        invit.answer(msg.accept);
    }

    @Receive
    public void changeMemberParameters(GuildChangeMemberParametersMessage msg) {
        Player player = this.player.get();
        WorldGuild guild = player.getGuild();

        if (guild == null) {
            client.write(BasicNoOperationMessage.i);
            return;
        }

        WorldGuildMember updated = guild.update(player, msg.memberId, msg.rank, msg.experienceGivenPercent,
                                                // int cast is ok as long as msg.rights is a ui32
                                                WorldGuildPermissions.of((int) msg.rights));

        if (updated == null) {
            client.write(BasicNoOperationMessage.i);
            return;
        }

        client.write(new GuildInformationsMemberUpdateMessage(updated.toGuildMember()));
    }

    @Receive
    public void kick(GuildKickRequestMessage msg) {
        Player player = this.player.get();
        WorldGuild guild = player.getGuild();

        if (guild == null) {
            client.write(BasicNoOperationMessage.i);
            return;
        }

        guild.kick(player, msg.kickedId);
    }

    @Listen
    public void onKicked(KickedGuildMemberEvent evt) {
        Player player = this.player.get();
        if (evt.getMember() == player) {
            guildSub.revoke();
            guildSub = null;
            client.write(GuildLeftMessage.i);
        } else {
            client.write(new GuildMemberLeavingMessage(evt.isKicked(), evt.getMember().getActorId()));
        }
    }
}
