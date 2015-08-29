package org.heat.world.controllers;

import com.ankamagames.dofus.network.enums.CharacterInventoryPositionEnum;
import com.ankamagames.dofus.network.enums.GameContextEnum;
import com.ankamagames.dofus.network.enums.ObjectErrorEnum;
import com.ankamagames.dofus.network.messages.game.basic.BasicNoOperationMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.objects.ObjectGroundListAddedMessage;
import com.ankamagames.dofus.network.messages.game.inventory.items.*;
import com.ankamagames.dofus.network.messages.game.shortcut.ShortcutBarRemovedMessage;
import com.github.blackrush.acara.Listen;
import org.fungsi.Either;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.shared.MoreFutures;
import org.heat.shared.Pair;
import org.heat.world.controllers.events.CreatePlayerEvent;
import org.heat.world.controllers.events.EnterContextEvent;
import org.heat.world.controllers.events.roleplay.EndPlayerMovementEvent;
import org.heat.world.controllers.events.roleplay.EquipItemEvent;
import org.heat.world.controllers.utils.Idling;
import org.heat.world.items.WorldItem;
import org.heat.world.items.WorldItemFactory;
import org.heat.world.items.WorldItemRepository;
import org.heat.world.players.Player;
import org.heat.world.players.items.PlayerItemWallet;
import org.heat.world.players.shortcuts.PlayerShortcut;
import org.heat.world.roleplay.environment.WorldMap;
import org.heat.world.roleplay.environment.WorldMapPoint;
import org.heat.world.roleplay.environment.WorldPosition;
import org.rocket.network.Controller;
import org.rocket.network.NetworkClient;
import org.rocket.network.Prop;
import org.rocket.network.Receive;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ankamagames.dofus.network.enums.CharacterInventoryPositionEnum.INVENTORY_POSITION_NOT_EQUIPED;
import static com.ankamagames.dofus.network.enums.ObjectErrorEnum.CANNOT_DROP_NO_PLACE;

@Controller
@Idling
public class ItemsController {
    @Inject NetworkClient client;
    @Inject Prop<Player> player;

    @Inject WorldItemRepository items;
    @Inject WorldItemFactory itemFactory;

    private void removeAnyShortcut(int itemUid) {
        List<PlayerShortcut> shortcuts = player.get().getShortcutBar().removeItemShortcut(itemUid);

        if (shortcuts.isEmpty()) return;

        client.transaction(tx -> {
            shortcuts.forEach(s -> tx.write(new ShortcutBarRemovedMessage(s.getBarType().value, s.getSlot())));
        });
    }

    private void publishEquipItemEvent(WorldItem before, WorldItem after) {
        if (!before.getItemType().isEquipment()) return;

        boolean apply = after.getPosition() != INVENTORY_POSITION_NOT_EQUIPED;
        client.getEventBus().publish(new EquipItemEvent(after, apply));
    }
    
    private Future<WorldItem> createAndSave(int gid, int quantity) {
        return items.save(itemFactory.create(gid, quantity));
    }

    @Listen
    public void onPlayerCreation(CreatePlayerEvent evt) {
        // DEBUG(world/frontend)
        Future<List<WorldItem>> items = Futures.collect(Arrays.asList(
                createAndSave(39, 1), // small owl amulet
                createAndSave(100, 2), // small wisdom ring
                createAndSave(2474, 1), // adventurer hat
                createAndSave(6801, 1), // winter cloak
                createAndSave(9002, 1) // phtalmo
        ));

        items.onSuccess(evt.getPlayer().getWallet()::addAll)
            .onSuccess(x -> client.write(new ObjectsAddedMessage(x.stream().map(WorldItem::toObjectItem))));
    }

    @Receive
    public void moveItem(ObjectSetPositionMessage msg) {
        Player player = this.player.get();
        PlayerItemWallet wallet = player.getWallet();

        CharacterInventoryPositionEnum position = CharacterInventoryPositionEnum.valueOf((byte) msg.position).get();
        int quantity = msg.quantity;

        // get item
        WorldItem item = wallet.findByUid(msg.objectUID).get();

        // verify movement validity
        if (!player.canMoveItemTo(item, position, quantity)) {
            client.write(new ObjectErrorMessage(ObjectErrorEnum.CANNOT_EQUIP_HERE.value));
            return;
        }

        // fork!
        Either<Pair<WorldItem, WorldItem>, WorldItem> fork = wallet.fork(item, quantity);
        if (fork.isLeft()) {
            // forked...
            Pair<WorldItem, WorldItem> forkPair = fork.left();
            WorldItem original = forkPair.first;
            WorldItem forked = forkPair.second;

            // merge or move!
            Either<WorldItem, WorldItem> mergeOrMove = wallet.mergeOrMove(forked, position);
            if (mergeOrMove.isLeft()) {
                // merged...
                WorldItem merged = mergeOrMove.left();

                MoreFutures.join(items.save(original), items.save(merged))
                    .onSuccess(pair -> {
                        wallet.update(pair.first);
                        wallet.update(pair.second);

                        client.transaction(tx -> {
                            tx.write(new ObjectQuantityMessage(pair.first.getUid(), pair.first.getQuantity()));
                            tx.write(new ObjectQuantityMessage(pair.second.getUid(), pair.second.getQuantity()));
                            tx.write(new InventoryWeightMessage(player.getWallet().getWeight(), player.getMaxWeight()));
                            tx.write(BasicNoOperationMessage.i);
                        });

                        publishEquipItemEvent(item, pair.second);
                    });
            } else {
                // moved...
                WorldItem moved = mergeOrMove.right();

                MoreFutures.join(items.save(original), items.save(moved))
                    .onSuccess(pair -> {
                        wallet.update(pair.first);
                        wallet.add(pair.second);

                        client.transaction(tx -> {
                            tx.write(new ObjectQuantityMessage(pair.first.getUid(), pair.first.getQuantity()));
                            tx.write(new ObjectAddedMessage(pair.second.toObjectItem()));
                            tx.write(new InventoryWeightMessage(player.getWallet().getWeight(), player.getMaxWeight()));
                            tx.write(BasicNoOperationMessage.i);
                        });

                        publishEquipItemEvent(item, pair.second);
                    });
            }
        } else {
            // not forked...

            // merge or move!
            Either<WorldItem, WorldItem> mergeOrMove = wallet.mergeOrMove(item, position);
            if (mergeOrMove.isLeft()) {
                // merged...
                WorldItem merged = mergeOrMove.left();

                removeAnyShortcut(item.getUid());

                MoreFutures.join(items.remove(item), items.save(merged))
                    .onSuccess(pair -> {
                        wallet.remove(item);
                        wallet.update(pair.second);

                        client.transaction(tx -> {
                            tx.write(new ObjectDeletedMessage(pair.first.getUid()));
                            tx.write(new ObjectQuantityMessage(pair.second.getUid(), pair.second.getQuantity()));
                            tx.write(new InventoryWeightMessage(player.getWallet().getWeight(), player.getMaxWeight()));
                            tx.write(BasicNoOperationMessage.i);
                        });

                        publishEquipItemEvent(item, pair.second);
                    });
            } else {
                // moved...
                WorldItem moved = mergeOrMove.right();

                items.save(moved)
                    .onSuccess(x -> {
                        wallet.update(x);
                        client.transaction(tx -> {
                            tx.write(new ObjectMovementMessage(x.getUid(), x.getPosition().value));
                            tx.write(new InventoryWeightMessage(player.getWallet().getWeight(), player.getMaxWeight()));
                            tx.write(BasicNoOperationMessage.i);
                        });

                        publishEquipItemEvent(item, x);
                    });
            }
        }
    }

    @Receive
    public void dropItem(ObjectDropMessage msg) {
        Player player = this.player.get();
        PlayerItemWallet wallet = player.getWallet();
        WorldMap map = player.getPosition().getMap();
        WorldMapPoint mapPoint = player.getPosition().getMapPoint();

        WorldItem item = wallet.findByUid(msg.objectUID).get();
        int quantity = msg.quantity;

        Either<Pair<WorldItem, WorldItem>, WorldItem> fork = wallet.fork(item, quantity);
        if (fork.isLeft()) {
            Pair<WorldItem, WorldItem> forkPair = fork.left();
            WorldItem original = forkPair.first;
            WorldItem forked = forkPair.second;

            if (!map.tryAddItem(() -> items.save(forked).get(), mapPoint, false)) {
                client.write(new ObjectErrorMessage(CANNOT_DROP_NO_PLACE.value));
                return;
            }

            items.save(original)
                .onSuccess(newOriginal -> {
                    wallet.update(newOriginal);

                    client.transaction(tx -> {
                        client.write(new ObjectQuantityMessage(newOriginal.getUid(), newOriginal.getQuantity()));
                        tx.write(new InventoryWeightMessage(wallet.getWeight(), player.getMaxWeight()));
                        tx.write(BasicNoOperationMessage.i);
                    });
                });
        } else {
            if (!map.tryAddItem(() -> item, mapPoint, false)) {
                client.write(new ObjectErrorMessage(CANNOT_DROP_NO_PLACE.value));
                return;
            }

            removeAnyShortcut(item.getUid());
            wallet.remove(item);

            client.transaction(tx -> {
                tx.write(new ObjectDeletedMessage(item.getUid()));
                tx.write(new InventoryWeightMessage(wallet.getWeight(), player.getMaxWeight()));
                tx.write(BasicNoOperationMessage.i);
            });
        }
    }

    @Listen
    public void showItemsOnMap(EnterContextEvent evt) {
        if (evt.getContext() != GameContextEnum.ROLE_PLAY) return;

        Player player = this.player.get();
        WorldMap map = player.getPosition().getMap();

        Map<WorldMapPoint, WorldItem> items = map.getItems();
        if (items.isEmpty()) return;

        // quick and dirty cellId-to-gid generation
        short[] cellIds = new short[items.size()];
        int[] gids = new int[items.size()];
        int[] index = {0};
        items.forEach((mapPoint, item) -> {
            cellIds[index[0]] = mapPoint.cellId;
            gids[index[0]] = item.getGid();
            index[0]++;
        });

        client.write(new ObjectGroundListAddedMessage(cellIds, gids));
    }

    @Listen
    public void tryGetItemOnMap(EndPlayerMovementEvent evt) {
        Player player = this.player.get();
        PlayerItemWallet wallet = player.getWallet();
        WorldPosition position = player.getPosition();

        Optional<WorldItem> option = position.getMap().tryRemoveItem(position.getMapPoint());
        if (!option.isPresent()) {
            return;
        }

        WorldItem item = option.get();

        wallet.merge(item)
                .ifLeft(merged -> {
                    items.remove(item); // remove and just forget it, nobody needs it anymore

                    items.save(merged)
                            .onSuccess(newMerged -> {
                                wallet.update(newMerged);
                                client.transaction(tx -> {
                                    tx.write(new ObjectQuantityMessage(newMerged.getUid(), newMerged.getQuantity()));
                                    tx.write(new InventoryWeightMessage(wallet.getWeight(), player.getMaxWeight()));
                                });
                            });
                })
                .ifRight(nonMerged -> {
                    wallet.add(nonMerged);

                    client.transaction(tx -> {
                        tx.write(new ObjectAddedMessage(nonMerged.toObjectItem()));
                        tx.write(new InventoryWeightMessage(wallet.getWeight(), player.getMaxWeight()));
                    });
                });
    }

    @Receive
    public void removeItem(ObjectDeleteMessage msg) {
        Player player = this.player.get();
        PlayerItemWallet wallet = player.getWallet();

        WorldItem item = wallet.findByUid(msg.objectUID).get();
        int quantity = msg.quantity;

        wallet.fork(item, quantity)
            .ifLeft(forkPair -> {
                WorldItem original = forkPair.first;

                // NOTE(Blackrush): we fork this item but never use the forked item to virtually delete it
                //WorldItem forked = forkPair.second;

                items.save(original)
                    .onSuccess(newOriginal -> {
                        wallet.update(newOriginal);

                        client.transaction(tx -> {
                            tx.write(new ObjectQuantityMessage(newOriginal.getUid(), newOriginal.getQuantity()));
                            tx.write(new InventoryWeightMessage(wallet.getWeight(), player.getMaxWeight()));
                        });
                    });
            })
            .ifRight(nonForked -> {
                removeAnyShortcut(nonForked.getUid());
                wallet.remove(nonForked);
                items.remove(nonForked);

                client.transaction(tx -> {
                    tx.write(new ObjectDeletedMessage(item.getUid()));
                    tx.write(new InventoryWeightMessage(wallet.getWeight(), player.getMaxWeight()));
                });
            });
    }

    @Listen
    public void refreshPlayerLook(EquipItemEvent evt) {
        player.get().refreshLook();
    }
}
