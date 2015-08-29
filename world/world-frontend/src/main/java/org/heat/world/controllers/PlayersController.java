package org.heat.world.controllers;

import com.ankamagames.dofus.network.enums.CharacterCreationResultEnum;
import com.ankamagames.dofus.network.enums.GameContextEnum;
import com.ankamagames.dofus.network.messages.authorized.AdminCommandMessage;
import com.ankamagames.dofus.network.messages.authorized.AdminQuietCommandMessage;
import com.ankamagames.dofus.network.messages.authorized.ConsoleMessage;
import com.ankamagames.dofus.network.messages.game.basic.TextInformationMessage;
import com.ankamagames.dofus.network.messages.game.character.choice.*;
import com.ankamagames.dofus.network.messages.game.character.creation.CharacterCreationRequestMessage;
import com.ankamagames.dofus.network.messages.game.character.creation.CharacterCreationResultMessage;
import com.ankamagames.dofus.network.messages.game.character.creation.CharacterNameSuggestionRequestMessage;
import com.ankamagames.dofus.network.messages.game.character.creation.CharacterNameSuggestionSuccessMessage;
import com.ankamagames.dofus.network.messages.game.character.deletion.CharacterDeletionErrorMessage;
import com.ankamagames.dofus.network.messages.game.character.deletion.CharacterDeletionRequestMessage;
import com.ankamagames.dofus.network.messages.game.character.stats.CharacterStatsListMessage;
import com.ankamagames.dofus.network.messages.game.context.notification.NotificationByServerMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.spell.SpellUpgradeFailureMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.spell.SpellUpgradeRequestMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.spell.SpellUpgradeSuccessMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.stats.StatsUpgradeRequestMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.stats.StatsUpgradeResultMessage;
import com.ankamagames.dofus.network.messages.game.initialization.CharacterLoadingCompleteMessage;
import com.ankamagames.dofus.network.messages.game.inventory.items.InventoryContentMessage;
import com.ankamagames.dofus.network.messages.game.inventory.items.InventoryWeightMessage;
import com.ankamagames.dofus.network.messages.game.inventory.spells.SpellListMessage;
import com.github.blackrush.acara.Listen;
import com.github.blackrush.acara.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.fungsi.Either;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.shared.Strings;
import org.heat.shared.stream.MoreCollectors;
import org.heat.world.backend.Backend;
import org.heat.world.commands.CommandManager;
import org.heat.world.controllers.events.*;
import org.heat.world.controllers.events.roleplay.EquipItemEvent;
import org.heat.world.controllers.utils.Idling;
import org.heat.world.controllers.utils.Md5;
import org.heat.world.controllers.utils.PlayerChoosing;
import org.heat.world.metrics.GameStats;
import org.heat.world.metrics.RegularStat;
import org.heat.world.players.*;
import org.heat.world.players.events.*;
import org.heat.world.players.metrics.PlayerSpell;
import org.heat.world.players.metrics.PlayerStatBook;
import org.heat.world.players.notifications.PlayerNotifRepository;
import org.heat.world.users.WorldUser;
import org.rocket.InjectConfig;
import org.rocket.network.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Instant;
import java.util.*;

import static com.ankamagames.dofus.network.enums.CharacterCreationResultEnum.ERR_NO_REASON;
import static com.ankamagames.dofus.network.enums.CharacterCreationResultEnum.OK;
import static com.ankamagames.dofus.network.enums.CharacterDeletionErrorEnum.DEL_ERR_BAD_SECRET_ANSWER;

@Controller
@Slf4j
public class PlayersController {
    @Inject NetworkClient client;
    @Inject Prop<WorldUser> user;
    @Inject MutProp<Player> player;

    @Inject PlayerRepository players;
    @Inject PlayerFactory playerFactory;
    @Inject PlayerRegistry playerRegistry;
    @Inject Backend backend;
    @Inject @Named("pseudo") Random randomPseudo;

    @InjectConfig("heat.world.player.remove-required-answer-min-level")
    int removeRequiredAnswerMinLevel;

    @Inject CommandManager manager;
    @Inject PlayerNotifRepository notifs;

    List<Player> cached;
    Subscription playerSub;
    Subscription worldSub;

    /**
     * Retrieve, sort, and cache players
     */
    List<Player> getPlayers() {
        if (cached == null) {
            // TODO(world/frontend): player load timeout
            cached = players.findByUserId(user.get().getId()).get();
            cached = new ArrayList<>(cached);
            Collections.sort(cached, Comparator.comparing(Player::getLastUsedAt).reversed());
        }
        return cached;
    }

    /**
     * Find a player from cache
     */
    Either<Player, Unit> findPlayer(int id) {
        return getPlayers().stream()
                .filter(x -> x.getId() == id)
                .collect(MoreCollectors.uniqueMaybe());
    }

    /**
     * Write player list
     */
    Future<Unit> writePlayerList() {
        return client.write(new CharactersListMessage(
                getPlayers().stream().map(Player::toCharacterBaseInformations),
                false // TODO(world/players): has startup actions
        ));
    }

    /**
     * Choose player dispatching events and other IO operations
     */
    Future<Unit> doChoose(Player player) {
        playerSub = player.getEventBus().subscribe(this);
        worldSub = playerRegistry.getEventBus().subscribe(this);

        return client.getEventBus().publish(new ChoosePlayerEvent(player)).toUnit()
                .flatMap(u -> players.save(player))
                .flatMap(x -> {
                    this.player.set(player);
                    return client.write(new CharacterSelectedSuccessMessage(player.toCharacterBaseInformations()));
                })
                .flatMap(x -> client.getEventBus().publish(new NewContextEvent(GameContextEnum.ROLE_PLAY)).toUnit())
                .flatMap(x -> client.write(CharacterLoadingCompleteMessage.i))
                ;
    }

    @Disconnect
    public Future<Unit> unsubscribeFromPlayer() {
        if (playerSub != null) {
            playerSub.revoke();
            playerSub = null;
        }

        if(worldSub != null) {
            worldSub.revoke();
            worldSub = null;
        }

        if (player.isDefined()) {
            Player p = this.player.get();
            return client.getEventBus().publish(new FreePlayerEvent(p))
                    .flatMap(answers -> players.save(p));
        }

        return Futures.unit();
    }

    @Receive
    @PlayerChoosing
    public void list(CharactersListRequestMessage msg) {
        writePlayerList();
    }

    @Receive
    @PlayerChoosing
    public void create(CharacterCreationRequestMessage msg) {
        // create player
        playerFactory.create(user.get(), msg.name, msg.breed, msg.sex, msg.colors, msg.cosmeticId)
        // persist it to database
        .flatMap(player -> players.create(player).map(x -> player))
        // publish it
        .flatMap(player -> client.getEventBus().publish(new CreatePlayerEvent(player)).map(x -> player))
        // notify it to backend
        .onSuccess(player -> backend.setNrPlayers(user.get().getId(), getPlayers().size() + 1))
        // notify it to client
        .flatMap(player -> client.write(new CharacterCreationResultMessage(OK.value))
                .flatMap(x -> doChoose(player)))
        .onFailure(err -> log.error("cannot create player", err))
        .mayRescue(cause -> {
            CharacterCreationResultEnum reason;
            if (cause instanceof PlayerCreationException) {
                reason = ((PlayerCreationException) cause).getReason();
            } else {
                reason = ERR_NO_REASON;
            }
            return client.write(new CharacterCreationResultMessage(reason.value));
        })
        ;
    }

    @Receive
    @PlayerChoosing
    public void remove(CharacterDeletionRequestMessage msg) {
        Player player = findPlayer(msg.characterId).left();

        if (player.getExperience().getCurrentLevel() >= removeRequiredAnswerMinLevel) {
            String hash = Md5.hash(player.getId(), user.get().getSecretAnswer());

            if (!msg.secretAnswerHash.equalsIgnoreCase(hash)) {
                client.write(new CharacterDeletionErrorMessage(DEL_ERR_BAD_SECRET_ANSWER.value));
                return;
            }

            // TODO DEL_ERR_TOO_MANY_CHAR_DELETION
        }

        cached.remove(player);
        players.remove(player).flatMap(u -> writePlayerList());
    }

    @Receive
    @PlayerChoosing
    public void suggestName(CharacterNameSuggestionRequestMessage msg) {
        // TODO(world/players): enable or disable pseudo suggestion
        client.write(new CharacterNameSuggestionSuccessMessage(Strings.randomPseudo(randomPseudo)));
    }

    @Receive
    @PlayerChoosing
    public void choose(CharacterSelectionMessage msg) {
        findPlayer(msg.id)
            .foldLeft(this::doChoose)
            .thenRight(x -> client.write(CharacterSelectedErrorMessage.i))
            .onFailure(err -> log.error("cannot choose player", err))
            ;
    }

    @Listen
    public void createRolePlayContext(CreateContextEvent evt) {
        if (evt.getContext() != GameContextEnum.ROLE_PLAY) return;

        Player player = this.player.get();

        client.transaction(tx -> {
            tx.write(new SpellListMessage(false, player.getSpells().toSpellItem()));

            tx.write(new CharacterStatsListMessage(player.toCharacterCharacteristicsInformations()));

            tx.write(new InventoryContentMessage(
                    player.getWallet().toObjectItem(),
                    player.getWallet().getKamas()
            ));
            tx.write(new InventoryWeightMessage(
                    player.getWallet().getWeight(),
                    player.getMaxWeight()
            ));
        });
    }

    @Listen
    public void appearHimselfOnline(ChoosePlayerEvent evt) {
        Player player = evt.getPlayer();

        player.setLastUsedAt(Instant.now());
        player.setStatus(Players.ONLINE_STATUS);

        playerRegistry.add(player);

        player.getUser().getEventBus().publish(new OnlinePlayerEvent(player));
    }

    @Listen
    public void appearHimselfOffline(FreePlayerEvent evt) {
        Player player = evt.getPlayer();

        player.setStatus(Players.OFFLINE_STATUS);

        playerRegistry.remove(player);

        player.getUser().getEventBus().publish(new OfflinePlayerEvent(player));
    }

    @Receive
    @Idling
    public void upgradeStat(StatsUpgradeRequestMessage msg) {
        Player player = this.player.get();
        GameStats<RegularStat> stat = GameStats.findBoostable(msg.statId).get();
        short upgraded = (short) player.getStats().upgradeStat(stat, msg.boostPoint);

        players.save(player);

        client.transaction(tx -> {
            tx.write(new StatsUpgradeResultMessage(upgraded));
            tx.write(new CharacterStatsListMessage(player.toCharacterCharacteristicsInformations()));
        });
    }

    @Receive
    @Idling
    public void upgradeSpell(SpellUpgradeRequestMessage msg) {
        Player player = this.player.get();
        PlayerSpell spell = player.getSpells().findById(msg.spellId).get();

        int cost = Players.getCostUpgradeSpell(spell.getLevel(), msg.spellLevel);

        if (cost > player.getStats().getSpellsPoints()) {
            client.write(SpellUpgradeFailureMessage.i);
        } else if (player.getExperience().getCurrentLevel() < spell.getMinPlayerLevel()) {
            client.write(SpellUpgradeFailureMessage.i);
        } else {
            player.getStats().plusSpellsPoints(-cost);
            spell.setLevel(msg.spellLevel);
            players.save(player);

            client.transaction(tx -> {
                tx.write(new SpellUpgradeSuccessMessage(spell.getId(), spell.getLevel()));
                tx.write(new CharacterStatsListMessage(player.toCharacterCharacteristicsInformations()));
            });
        }
    }

    @Listen
    public void applyItemToStatBook(EquipItemEvent evt) {
        Player player = this.player.get();
        PlayerStatBook stats = player.getStats();
        if (evt.isApply()) {
            stats.apply(evt.getItem());
        } else {
            stats.unapply(evt.getItem());
        }

        client.write(new CharacterStatsListMessage(player.toCharacterCharacteristicsInformations()));
    }

    @Listen
    public Future<KickPlayerEvent> kickRequested(KickPlayerEvent evt) {
        return client.close().map(x -> KickPlayerEvent.ACK);
    }

    @Listen
    public void noticed(NoticePlayerEvent evt) {
        String[] args = Arrays.stream(evt.getArgs())
                .map(String::valueOf).toArray(String[]::new);

        client.write(new TextInformationMessage(
                evt.getType().value,
                (short) evt.getImId(),
                args
        ));
    }

    @Listen
    public void consoleNoticed(NoticeAdminEvent evt) {
        client.write(new ConsoleMessage(
                evt.getType(),
                evt.getText())
        );
    }

    @Receive
    public void quietCommandSent(AdminQuietCommandMessage msg) {
        manager.execute(player.get(), msg.content, true);
    }

    @Receive
    public void commandSent(AdminCommandMessage msg) {
        manager.execute(player.get(), msg.content, true);
    }

    @Listen
    public void inform(InformPlayerEvent evt) {
        client.write(new NotificationByServerMessage(
                evt.getId(),
                evt.getArgs(),
                true
        ));
    }
}
