package org.heat.world.players.items;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.heat.world.items.WorldItem;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface PlayerItemRepository {
    Future<List<WorldItem>> findItemsByPlayer(int playerId);
    Future<Unit> persist(int playerId, int itemId);
    Future<Unit> persistAll(int playerId, IntStream itemIds);
    Future<Unit> remove(int playerId, int itemId);

    default Future<Unit> persist(int playerId, WorldItem item) {
        return persist(playerId, item.getUid());
    }

    default Future<Unit> persistAll(int playerId, Stream<WorldItem> items) {
        return persistAll(playerId, items.mapToInt(WorldItem::getUid));
    }

    default Future<Unit> remove(int playerId, WorldItem item) {
        return remove(playerId, item.getUid());
    }
}
