package org.heat.world.players;

import com.ankamagames.dofus.datacenter.breeds.Breed;
import com.ankamagames.dofus.network.enums.*;
import com.ankamagames.dofus.network.types.game.character.alignment.ActorAlignmentInformations;
import com.ankamagames.dofus.network.types.game.character.alignment.ActorExtendedAlignmentInformations;
import com.ankamagames.dofus.network.types.game.character.characteristic.CharacterCharacteristicsInformations;
import com.ankamagames.dofus.network.types.game.character.characteristic.CharacterSpellModification;
import com.ankamagames.dofus.network.types.game.character.choice.CharacterBaseInformations;
import com.ankamagames.dofus.network.types.game.character.restriction.ActorRestrictionsInformations;
import com.ankamagames.dofus.network.types.game.character.status.PlayerStatus;
import com.ankamagames.dofus.network.types.game.context.roleplay.*;
import com.ankamagames.dofus.network.types.game.context.roleplay.party.PartyInvitationMemberInformations;
import com.ankamagames.dofus.network.types.game.context.roleplay.party.PartyMemberInformations;
import com.ankamagames.dofus.network.types.game.context.roleplay.party.companion.PartyCompanionMemberInformations;
import com.ankamagames.dofus.network.types.game.friend.FriendOnlineInformations;
import com.ankamagames.dofus.network.types.game.guild.GuildMember;
import com.github.blackrush.acara.EventBus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.heat.UserRank;
import org.heat.shared.stream.MoreCollectors;
import org.heat.world.chat.*;
import org.heat.world.commands.CommandSender;
import org.heat.world.groups.WorldGroupMember;
import org.heat.world.guilds.WorldGuild;
import org.heat.world.guilds.WorldGuildMember;
import org.heat.world.guilds.WorldGuildPermissions;
import org.heat.world.items.WorldItem;
import org.heat.world.items.WorldItemType;
import org.heat.world.metrics.GameStats;
import org.heat.world.players.events.InformPlayerEvent;
import org.heat.world.players.contacts.ContactList;
import org.heat.world.players.events.KickPlayerEvent;
import org.heat.world.players.events.NoticeAdminEvent;
import org.heat.world.players.events.NoticePlayerEvent;
import org.heat.world.players.events.PlayerTeleportEvent;
import org.heat.world.players.items.PlayerItemWallet;
import org.heat.world.players.metrics.PlayerExperience;
import org.heat.world.players.metrics.PlayerSpellBook;
import org.heat.world.players.metrics.PlayerStatBook;
import org.heat.world.players.shortcuts.PlayerShortcutBar;
import org.heat.world.roleplay.WorldActor;
import org.heat.world.roleplay.WorldActorLook;
import org.heat.world.roleplay.WorldHumanoidActor;
import org.heat.world.roleplay.environment.WorldMapPoint;
import org.heat.world.roleplay.environment.WorldPosition;
import org.heat.world.trading.impl.player.PlayerTrader;
import org.heat.world.users.WorldUser;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.ankamagames.dofus.network.enums.CharacterInventoryPositionEnum.*;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Player
        implements Serializable,
            WorldHumanoidActor,
            PlayerTrader,
            WorldGroupMember,
            WorldMessageReceiver,
            CommandSender,
            WorldGuildMember
{
    EventBus          eventBus;
    int               id;
    WorldUser         user;
    String            name;
    Breed             breed;
    boolean           sex;
    WorldActorLook    look;
    WorldPosition     position;
    PlayerExperience  experience;
    PlayerStatBook    stats;
    PlayerSpellBook   spells;
    PlayerItemWallet  wallet;
    PlayerShortcutBar shortcutBar;
    ContactList       contacts;

    /**
     * Those next fields won't get loaded until explicitly wanted.
     */
    WorldGuild            guild;
    WorldGuildPermissions guildPermissions;
    short                 guildRank;
    byte                  guildGivenExperiencePercent;

    Instant lastUsedAt;
    PlayerStatus status = Players.OFFLINE_STATUS;

    // lombok auto-generates a #isSex() which is invalid here
    public boolean getSex() {
        return sex;
    }

    public int getUserId() {
        return user.getId();
    }

    // kinda hacky but works as long as PlayerStatusEnum doesnt change
    public boolean isConnected() {
        return status.statusId >= 10;
    }

    public void moveTo(WorldMapPoint point, DirectionsEnum dir) {
        setPosition(getPosition().moveTo(point, dir));
    }

    public void teleportTo(int mapId, WorldMapPoint point) {
        eventBus.publish(new PlayerTeleportEvent(mapId, point));
    }

    public CharacterBaseInformations toCharacterBaseInformations() {
        return new CharacterBaseInformations(id, (short) experience.getCurrentLevel(), name, look.toEntityLook(),
                                             (byte) breed.getId(), sex);
    }

    public ActorAlignmentInformations toActorAlignmentInformations() {
        // TODO(world/players): alignment
        return new ActorAlignmentInformations();
    }

    public FriendOnlineInformations toFriendInformations() {
        return new FriendOnlineInformations(user.getId(), user.getNickname(),
                                            PlayerStateEnum.GAME_TYPE_ROLEPLAY.value,//TODO roleplay/fight state
                                            (int) lastUsedAt.getEpochSecond(),
                                            0 /*achievement*/,
                                            id, name, (short) experience.getCurrentLevel(),
                                            (byte) 0/*alignment*/, (byte) breed.getId(), sex,
                                            getBasicGuildInformations(),
                                            (byte) 0/*smiley*/, status);
    }

    public BasicGuildInformations getBasicGuildInformations() {
        return guild != null ? guild.toBasicGuildInformations()
                             : new BasicGuildInformations(0, "");
    }

    @Override
    public int getActorId() {
        return id;
    }

    @Override
    public int getActorUserId() {
        return getUserId();
    }

    @Override
    public String getActorName() {
        return name;
    }

    @Override
    public WorldActorLook getActorLook() {
        return look;
    }

    @Override
    public WorldPosition getActorPosition() {
        return position;
    }

    @Override
    public boolean getActorSex() {
        return sex;
    }

    @Override
    public BreedEnum getActorBreed() {
        return BreedEnum.valueOf((byte) breed.getId()).get();
    }

    @Override
    public PlayerStatus toPlayerStatus() {
        return getStatus();
    }

    @Override
    public int getSpeakerId() {
        return id;
    }

    @Override
    public int getSpeakerUserId() {
        return user.getId();
    }

    @Override
    public String getSpeakerName() {
        return name;
    }

    @Override
    public UserRank getSpeakerRank() {
        return user.getRank();
    }

    @Override
    public ActorRestrictionsInformations toActorRestrictionsInformations() {
        // TODO(world/players): actor restrictions
        return new ActorRestrictionsInformations();
    }

    public Stream<HumanOption> toHumanOption() {
        List<HumanOption> options = new LinkedList<>();

        WorldGuild guild = this.guild;
        if (guild != null) {
            options.add(new HumanOptionGuild(guild.toGuildInformations()));
        }

        // TODO(world/players): human options

        return options.stream();
    }

    @Override
    public HumanInformations toHumanInformations() {
        return new HumanInformations(toActorRestrictionsInformations(), sex, toHumanOption());
    }

    @Override
    public GameRolePlayCharacterInformations toGameRolePlayActorInformations() {
        return new GameRolePlayCharacterInformations(id, look.toEntityLook(),
                                                     position.toEntityDispositionInformations(), name,
                                                     toHumanInformations(), getUserId(),
                                                     toActorAlignmentInformations());
    }

    public CharacterCharacteristicsInformations toCharacterCharacteristicsInformations() {
        CharacterCharacteristicsInformations res = new CharacterCharacteristicsInformations();

        // TODO(world/players): alignment
        res.alignmentInfos = new ActorExtendedAlignmentInformations();
        // TODO(world/players): spell modifications
        res.spellModifications = new CharacterSpellModification[0];

        res.experience = experience.getCurrent();
        res.experienceLevelFloor = experience.getStep().getTop();
        res.experienceNextLevelFloor = experience.getStep().getNext().orElse(experience.getStep()).getTop();

        res.statsPoints = stats.getStatsPoints();
        res.spellsPoints = stats.getSpellsPoints();
        Players.populateCharacterCharacteristicsInformations(stats, res);

        return res;
    }

    @Override
    public PartyMemberInformations toPartyMemberInformations() {
        return new PartyMemberInformations(
            id,
            (short) experience.getCurrentLevel(),
            name,
            look.toEntityLook(),
            (byte) breed.getId(),
            sex,
            stats.get(GameStats.LIFE).getCurrent(),
            stats.get(GameStats.LIFE).getMax(),
            stats.get(GameStats.PROSPECTING).getTotal(),
            (short) 1, // TODO(world/players): regen rate
            stats.get(GameStats.INITIATIVE).getTotal(),
            (byte) 0, // TODO(world/players): alignment side
            (short) position.getMapCoordinates().first,
            (short) position.getMapCoordinates().second,
            position.getMapId(),
            (short) position.getSubAreaId(),
            toPlayerStatus(),
            new PartyCompanionMemberInformations[0] // TODO(world/players): companions
        );
    }

    @Override
    public PartyInvitationMemberInformations toPartyInvitationMemberInformations() {
        return new PartyInvitationMemberInformations(
                id,
                (short) experience.getCurrentLevel(),
                name,
                look.toEntityLook(),
                (byte) breed.getId(),
                sex,
                (short) position.getMapCoordinates().first,
                (short) position.getMapCoordinates().second,
                position.getMapId(),
                (short) position.getSubAreaId(),
                new PartyCompanionMemberInformations[0] // TODO(world/players): companions
        );
    }

    @Override
    public GuildMember toGuildMember() {
        return new GuildMember(id, (short) experience.getCurrentLevel(), name, (byte) breed.getId(), sex,
                               guildRank, 0.0, guildGivenExperiencePercent, guildPermissions.getBits(),
                               isConnected() ? (byte) 1 : (byte) 0,
                               (byte) 0, // TODO(world/players): alignmennt
                               (int) Duration.between(Instant.now(), user.getLastConnection()).toHours(),
                               (byte) 0, // mood smiley id
                               user.getId(),
                               0, // achievement points
                               toPlayerStatus());
    }

    @SuppressWarnings({"SimplifiableIfStatement", "RedundantIfStatement"})
    public boolean canMoveItemTo(WorldItem item, CharacterInventoryPositionEnum to, int quantity) {
        /**
         * TODO(world/items): item movement validity
         * you cannot equip a pet if there is a mount
         */

        // we only worry if we want to equip an item
        if (to == INVENTORY_POSITION_NOT_EQUIPED) {
            return true;
        }

        // you cannot equip if target position is already taken
        if (wallet.findByPosition(to).findAny().isPresent()) {
            return false;
        }

        // you cannot equip a greater level item
        if (item.getTemplate().getLevel() > getExperience().getCurrentLevel()) {
            return false;
        }

        // this item type can not be moved here
        if (!item.getItemType().canBeMovedTo(to)) {
            return false;
        }

        // make sure we do not equip a ring twice
        if (item.getItemType() == WorldItemType.RING) {
            CharacterInventoryPositionEnum backwards = to == INVENTORY_POSITION_RING_LEFT
                    ? INVENTORY_POSITION_RING_RIGHT
                    : INVENTORY_POSITION_RING_LEFT;

            WorldItem otherRing = wallet.findByPosition(backwards).collect(MoreCollectors.uniqueOption()).orElse(null);

            if (otherRing != null && otherRing.getGid() == item.getGid()
                    && item.getTemplate().getItemSetId() != -1) {
                return false;
            }
        }

        // we want to equip only *one* item
        if (item.getItemType().isEquipment() && quantity != 1) {
            return false;
        }

        return true;
    }

    /**
     * Get max transportable weight. See http://dofuswiki.wikia.com/wiki/Characteristic#Pods_.28Carrying_Capacity.29
     *
     * <p>
     * This statistic determines the number of items you can carry. The base value is 1000.
     * Each profession level of the character gives +5 pods, and each level 100 profession gives an additional +1000 pods.
     * Strength also affects carrying capacity, at the rate of 5 pods per strength point.
     * Pods can also be obtained from Pods equipment.
     *
     * @return an integer
     */
    public int getMaxWeight() {
        return Players.BASE_TRANSPORTABLE_WEIGHT
                + stats.get(GameStats.STRENGTH).getSafeTotal() * 5
                + stats.get(GameStats.PODS).getTotal()
                // TODO(world/players): jobs affect transportable weight
                ;
    }

    public Future<Unit> kick() {
        return getEventBus().publish(KickPlayerEvent.INSTANCE)
            .filter(answers -> answers.contains(KickPlayerEvent.ACK))
            .toUnit();
    }

    public void refreshLook() {
        look = look.withItems(wallet.findMapDisplayable());
        position.getMap().refreshActor(this);
    }

    @Override
    public int getChannelId() {
        return ChatActivableChannelsEnum.PSEUDO_CHANNEL_PRIVATE.value;
    }

    @Override
    public boolean accepts(WorldSpeaker speaker) {
        return true;
    }

    @Override
    public void speak(WorldSpeaker speaker, WorldChannelMessage message) {
        WorldChannelEnvelope envelope = new WorldChannelEnvelope(
                speaker,
                new PrivateChannelMessage.Resolved(this, message),
                Instant.now()
        );

        eventBus.publish(envelope);

        if (speaker instanceof WorldMessageReceiver) {
            ((WorldMessageReceiver) speaker).getEventBus().publish(envelope);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;
        return id == player.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public static Optional<Player> asPlayer(WorldActor actor) {
        if (actor instanceof Player) {
            return Optional.of((Player) actor);
        }
        return Optional.empty();
    }

    /**
     * Send a message
     * @param id InformationMessage id (see at d2reader)
     * @param type Type of message
     * @param args Arguments of
     */
    public void notice(int id, TextInformationTypeEnum type, Object... args) {
        eventBus.publish(new NoticePlayerEvent(
                id,
                type,
                args
        ));
    }

    public void noticeConsole(boolean error, String msg, Object... args) {
        if(args.length > 0)
            msg = String.format(msg, args);

        eventBus.publish(new NoticeAdminEvent(
                (byte) (error ? 2 : 1),
                msg
        ));
    }

    public String getClickerName() {
        return "<b>{player," + name + "," + id + "::" + name + "}</b>";
    }

    /**
     * @param error is it an error ?
     * @param console
     * @param text text[0] can be the title of your error if it's an error.
     *             Else it represents your text which gonna to be showed.
     *             Then, text[1] will be you text and text[0] your title if
     *             it's an error.
     */
    @Override
    public void reply(boolean error, boolean console, Object... text) {
        int length = text.length;
        Object[] args;

        if(length > 1)
            args = new Object[] {"<b>" + text[0] + "</b>", text[1]};
        else if(error)
            args = new Object[] {"<b>Erreur</b>", text[0]};
        else
            args = new Object[] {text[0]};

        if(!console) {
            notice((byte) (error ? 16 : 0), error
                            ? TextInformationTypeEnum.TEXT_INFORMATION_ERROR
                            : TextInformationTypeEnum.TEXT_INFORMATION_MESSAGE,
                    args
            );
        } else {
            String msg = String.valueOf(args[0]);

            if(args.length > 1)
                msg = msg + ": "+args[1];

            noticeConsole(error, msg);
        }
    }

    @Override
    public UserRank getRank() {
        return user.getRank();
    }


    /**
     * Send a notification
     * @param id put a notifId
     * @param args args of notifId
     */
    public void sendNotification(int id, String... args) {
        eventBus.publish(new InformPlayerEvent(
                id,
                args
        ));
    }
}
