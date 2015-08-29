package org.heat.world.items;

import com.ankamagames.dofus.network.enums.CharacterInventoryPositionEnum;
import org.fungsi.Either;
import org.heat.shared.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class MapItemBag implements WorldItemBag {
    private final Map<Integer, WorldItem> map;

    MapItemBag(Map<Integer, WorldItem> map) {
        this.map = map;
    }

    public static MapItemBag newHashMapItemBag() {
        return new MapItemBag(new HashMap<>());
    }

    public static MapItemBag newConcurrentHashMapItemBag() {
        return new MapItemBag(new ConcurrentHashMap<>());
    }

    @Override
    public Optional<WorldItem> findByUid(int uid) {
        return Optional.ofNullable(map.get(uid));
    }

    @Override
    public Stream<WorldItem> getItemStream() {
        return map.values().stream();
    }

    @Override
    public void add(WorldItem item) {
        if (map.containsKey(item.getUid())) {
            throw new IllegalArgumentException("bag already contains " + item);
        }

        map.put(item.getUid(), item);
    }

    @Override
    public void addAll(List<WorldItem> items) {
        items.forEach(this::add);
    }

    @Override
    public void update(WorldItem item) {
        WorldItem old = map.get(item.getUid());
        if (old == null) {
            throw new NoSuchElementException("bag does not contain " + item);
        }
        if (old.getVersion() > item.getVersion()) {
            throw new OutdatedItemException(item, old);
        }

        map.put(item.getUid(), item);
    }

    @Override
    public void remove(WorldItem item) {
        WorldItem actual = map.get(item.getUid());
        if (actual == null) {
            throw new NoSuchElementException("bag does not contain " + item);
        }
        if (actual.getVersion() != item.getVersion()) {
            throw new OutdatedItemException(item, actual);
        }

        map.remove(item.getUid());
    }

    @Override
    public Optional<WorldItem> tryRemove(int uid) {
        return Optional.ofNullable(map.remove(uid));
    }

    @Override
    public Either<Pair<WorldItem, WorldItem>, WorldItem> fork(WorldItem item, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive but was equal to " + quantity);
        }
        if (quantity > item.getQuantity()) {
            throw new IllegalArgumentException("quantity must be lower or equal to " + item.getQuantity() + " but was equal to " + quantity);
        }

        if (quantity == item.getQuantity()) {
            return Either.right(item);
        }

        WorldItem forked = item.fork(quantity);
        return Either.left(Pair.of(item.plusQuantity(-quantity), forked));
    }

    @Override
    public Either<WorldItem, WorldItem> mergeOn(WorldItem item, CharacterInventoryPositionEnum position) {
        requireNonNull(item, "item");
        requireNonNull(position, "position");

        Optional<WorldItem> opt = findByPosition(position)
                .filter(x -> WorldItem.compare(item, x) == 0)
                .findFirst();

        if (!opt.isPresent()) {
            return Either.right(item);
        }

        WorldItem same = opt.get();
        return Either.left(same.plusQuantity(item.getQuantity()));
    }
}
