package org.heat.world.controllers;

import com.ankamagames.dofus.network.messages.game.character.stats.LifePointsRegenBeginMessage;
import com.ankamagames.dofus.network.messages.game.context.*;
import com.ankamagames.dofus.network.messages.game.context.roleplay.*;
import com.ankamagames.dofus.network.messages.game.context.roleplay.objects.ObjectGroundAddedMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.objects.ObjectGroundRemovedMessage;
import com.github.blackrush.acara.Listen;
import com.github.blackrush.acara.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.world.controllers.events.CreateContextEvent;
import org.heat.world.controllers.events.DestroyContextEvent;
import org.heat.world.controllers.events.EnterContextEvent;
import org.heat.world.controllers.events.QuitContextEvent;
import org.heat.world.controllers.events.roleplay.EndPlayerMovementEvent;
import org.heat.world.controllers.events.roleplay.StartPlayerMovementEvent;
import org.heat.world.controllers.utils.Basics;
import org.heat.world.controllers.utils.Idling;
import org.heat.world.controllers.utils.RolePlaying;
import org.heat.world.players.Player;
import org.heat.world.players.events.PlayerTeleportEvent;
import org.heat.world.roleplay.WorldAction;
import org.heat.world.roleplay.WorldActor;
import org.heat.world.roleplay.environment.*;
import org.heat.world.roleplay.environment.events.*;
import org.rocket.InjectConfig;
import org.rocket.network.*;

import javax.inject.Inject;
import java.util.stream.Stream;

import static com.ankamagames.dofus.network.enums.GameContextEnum.ROLE_PLAY;

@Controller
@RolePlaying
@Slf4j
public class RolePlayController {
    @Inject NetworkClient client;

    @Inject Prop<Player> player;
    @Inject MutProp<WorldAction> currentAction;

    @Inject WorldPositioningSystem wps;

    Subscription mapSub;

    @InjectConfig("heat.world.maps.key") String mapsKey;

    @Receive
    public void createContext(GameContextCreateRequestMessage msg) {
        client.transaction(tx -> {
            tx.write(new GameContextDestroyMessage());
            tx.write(new GameContextCreateMessage(ROLE_PLAY.value));

            // TODO(world/frontend): life points regen
            tx.write(new LifePointsRegenBeginMessage((short) 1));
        });

        client.getEventBus().publish(new CreateContextEvent(ROLE_PLAY));
    }

    @Disconnect
    public Future<?> destroyContext() {
        if (player.isDefined()) {
            return client.getEventBus().publish(new QuitContextEvent(ROLE_PLAY))
            .flatMap(o -> client.getEventBus().publish(new DestroyContextEvent(ROLE_PLAY)));
        }

        return Futures.unit();
    }

    @Receive
    public void getMapInfos(MapInformationsRequestMessage msg) {
        WorldPosition position = player.get().getPosition();

        if (msg.mapId != position.getMapId()) {
            // NOTE(Blackrush): just log a warning for now
            log.warn("client {} requested wrong map informations", client);
        }
	
        client.write(new MapComplementaryInformationsDataMessage(
                (short) position.getSubAreaId(),
                position.getMapId(),
                Stream.empty(), // TODO(world): houses
                position.getMap().getActorStream().map(WorldActor::toGameRolePlayActorInformations),
                Stream.empty(), // TODO(world): interactive elements
                Stream.empty(), // TODO(world): stated elements
                Stream.empty(), // TODO(world): map obstacles
                Stream.empty()  // TODO(world): fights
        ));

        client.getEventBus().publish(new EnterContextEvent(ROLE_PLAY));
    }

    @Receive
    public void changeMap(ChangeMapMessage msg) {
        Player player = this.player.get();
        WorldPosition newPos = player.getPosition().goToMap(msg.mapId);

        client.getEventBus().publish(new QuitContextEvent(ROLE_PLAY))
                .flatMap(x -> {
                    player.setPosition(newPos);
                    return client.getEventBus().publish(new CreateContextEvent(ROLE_PLAY));
                });
    }

    @Receive
    @Idling
    public void move(GameMapMovementRequestMessage msg) {
        Player player = this.player.get();

        WorldMapPath path = WorldMapPath.parse(player.getPosition(), msg.keyMovements);
        //noinspection ConstantConditions
        if (!path.isValid()) {
            throw new InvalidMapPathException();
        }

        WorldMovement movement = new WorldMovement(player, path);

        client.getEventBus().publish(new StartPlayerMovementEvent(movement))
            .onSuccess(answers -> {
                currentAction.set(movement);
                player.getPosition().getMap().moveActor(player, path);
            })
            .onFailure(err -> log.debug("wasn't able to move", err))
            ;
    }

    @Receive
    public void moveConfirmation(GameMapMovementConfirmMessage msg) {
        WorldMovement movement = (WorldMovement) currentAction.get();
        currentAction.forget();

        Player player = this.player.get();
        WorldMapPath path = movement.getPath();
        player.moveTo(path.target().getPoint(), path.target().getDir());

        movement.notifyEnd();

        client.write(Basics.noop());
        client.getEventBus().publish(new EndPlayerMovementEvent(movement));
    }

    @Receive
    public void moveCancellation(GameMapMovementCancelMessage msg) {
        WorldMovement movement = (WorldMovement) currentAction.get();
        currentAction.forget();

        Player player = this.player.get();
        WorldMapPath path = movement.getPath();
        WorldMapPoint cancellationPoint = WorldMapPoint.of(msg.cellId).get();

        if (!path.contains(cancellationPoint)) {
            throw new IllegalArgumentException("you can not cancel your path here");
        }
        player.moveTo(cancellationPoint, player.getPosition().getDirection());

        movement.notifyCancellation(cancellationPoint);

        client.write(Basics.noop());
        client.getEventBus().publish(new EndPlayerMovementEvent(movement));
    }

    @Listen
    public void createRolePlayContext(CreateContextEvent evt) {
        Player player = this.player.get();

        mapSub = player.getPosition().getMap().getEventBus().subscribe(this);
        player.getPosition().getMap().addActor(player);

        client.transaction(tx -> {
            tx.write(new CurrentMapMessage(player.getPosition().getMapId(), mapsKey));

            tx.write(Basics.time());
            tx.write(Basics.noop());
        });
    }

    @Listen
    public void quitRolePlayContext(QuitContextEvent evt) {
        if (evt.getContext() != ROLE_PLAY) return;

        Player player = this.player.get();
        player.getPosition().getMap().removeActor(player);
        mapSub.revoke();
        mapSub = null;
    }

    @Listen
    public void actorEntranceOnMap(ActorEntranceEvent evt) {
        if (evt.isEntering()) {
            client.write(new GameRolePlayShowActorMessage(evt.getActor().toGameRolePlayActorInformations()));
        } else {
            client.write(new GameContextRemoveElementMessage(evt.getActor().getActorId()));
        }
    }

    @Listen
    public void actorRefreshOnMap(ActorRefreshEvent evt) {
        client.write(new GameContextRefreshEntityLookMessage(
                evt.getActor().getActorId(),
                evt.getActor().getActorLook().toEntityLook()
        ));
    }

    @Listen
    public void actorMovementOnMap(ActorMovementEvent evt) {
        client.write(new GameMapMovementMessage(evt.getPath().export(), evt.getActor().getActorId()));
    }

    @Listen
    public void addItemOnMap(MapItemAddEvent evt) {
        client.write(new ObjectGroundAddedMessage(evt.getMapPoint().cellId, (short) evt.getItem().getGid()));
    }

    @Listen
    public void removeItemFromMap(MapItemRemoveEvent evt) {
        client.write(new ObjectGroundRemovedMessage(evt.getMapPoint().cellId));
    }

    public void teleportTo(PlayerTeleportEvent evt) {
        WorldPosition position = wps.locate(
                evt.getMapId(),
                evt.getPoint(),
                player.get().getPosition().getDirection());

        client.getEventBus().publish(new QuitContextEvent(ROLE_PLAY))
                .flatMap(x -> {
                    player.get().setPosition(position);
                    return client.getEventBus().publish(new CreateContextEvent(ROLE_PLAY));
                });
    }
}
