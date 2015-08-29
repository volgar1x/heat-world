package org.heat.world.roleplay.environment;

import com.ankamagames.dofus.network.enums.DirectionsEnum;
import com.github.blackrush.acara.EventBus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.heat.dofus.d2p.maps.DofusMap;
import org.heat.world.items.WorldItem;
import org.heat.world.roleplay.WorldActor;
import org.heat.world.roleplay.environment.events.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.ankamagames.dofus.network.enums.DirectionsEnum.*;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public final class WorldMap extends DofusMap {
    /**
     * From {@link java.util.concurrent.ConcurrentHashMap}
     * " the (estimated) number of elements needed for this operation to be executed in parallel "
     */
    public static final int PARALLELISM_THRESHOLD = 100;


    @Getter final EventBus eventBus;
    final ConcurrentHashMap<Integer, WorldActor> actors = new ConcurrentHashMap<>();
    final Map<WorldMapPoint, WorldItem> items = new HashMap<>();

    public Stream<WorldActor> getActorStream() {
        return actors.values().stream();
    }

    public Map<WorldMapPoint, WorldItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Add an actor to the map
     * @param actor a non-null actor
     * @throws java.lang.IllegalArgumentException if the map already contains the actor
     * @throws java.lang.NullPointerException if the parameter is null
     */
    public void addActor(WorldActor actor) {
        requireNonNull(actor, "actor");

        // NOTE(Blackrush): 24 aug 2014
        //      I really don't know if it's useful to throw an exception
        //      if it already contains the given actor.
        //      It seems reasonable to just ignore IMHO

        if (actors.putIfAbsent(actor.getActorId(), actor) == null) {
            eventBus.publish(new ActorEntranceEvent(this, actor, true));
        }
    }

    /**
     * Remove an actor from the map
     * @param actor a non-null actor
     * @throws java.lang.IllegalArgumentException if the map doesn't contain the actor
     * @throws java.lang.NullPointerException if the parameter is null
     */
    public void removeActor(WorldActor actor) {
        requireNonNull(actor, "actor");

        // NOTE(Blackrush): 24 aug 2014
        //      I really don't know if it's useful to throw an exception
        //      if it already contains the given actor.
        //      It seems reasonable to just ignore IMHO

        if (actors.remove(actor.getActorId()) != null) {
            eventBus.publish(new ActorEntranceEvent(this, actor, false));
        }
    }

    /**
     * Refresh an actor on the map
     * @param actor a non-null actor
     * @throws java.lang.IllegalArgumentException if the map already contains the actor
     * @throws java.lang.NullPointerException if the parameter is null
     */
    public void refreshActor(WorldActor actor) {
        requireNonNull(actor, "actor");

        // NOTE(Blackrush): 24 aug 2014
        //      Although it ignores re-add and re-remove
        //      I think it's kind of handy to throw an exception if you try to update an unknown actor

        if (!actors.containsKey(actor.getActorId())) {
            throw new IllegalArgumentException();
        }

        eventBus.publish(new ActorRefreshEvent(this, actor));
    }

    /**
     * Move an actor on the map
     * @param actor a non-null actor
     * @param path a non-null path
     * @throws java.lang.IllegalArgumentException if the map already contains the actor
     * @throws java.lang.NullPointerException if a parameter is null
     */
    public void moveActor(WorldActor actor, WorldMapPath path) {
        requireNonNull(actor, "actor");
        requireNonNull(path, "path");

        // NOTE(Blackrush): 24 aug 2014
        //      Although it ignores re-add and re-remove
        //      I think it's kind of handy to throw an exception if you try to move an unknown actor

        if (!actors.containsKey(actor.getActorId())) {
            throw new IllegalArgumentException();
        }

        eventBus.publish(new ActorMovementEvent(this, actor, path));
    }

    /**
     * Determine whether or not this map has an actor on the given map point
     * @param mapPoint a non-null map point
     * @return {@code true} if it has an actor on the given map point, {@code false} otherwise
     */
    public boolean hasActorOn(WorldMapPoint mapPoint) {
        // NOTE(Blackrush): 24 aug 2014
        //      ConcurrentHashMap#searchValues might speed-up search
        return actors.values().stream().anyMatch(x -> x.getActorPosition().getMapPoint().equals(mapPoint));
    }

    /**
     * Find an actor on the map by its id
     * @param id a integer representing an actor id
     * @return an optional actor having given id
     */
    public Optional<WorldActor> findActor(int id) {
        return Optional.ofNullable(actors.get(id));
    }

    /**
     * Say if an actor is on the map or not.
     * @param actor a non-null actor
     * @return {@code true} if he's present on the map, {@code false} otherwise
     */
    public boolean hasActor(WorldActor actor) {
        return actors.containsValue(actor);
    }

    /**
     * Find an actor on the map according to a predicate
     * @param fn a non-null predicate
     * @return an optional actor matching given predicate
     */
    public Optional<WorldActor> findActor(Predicate<WorldActor> fn) {
        return Optional.ofNullable(
            actors.searchValues(PARALLELISM_THRESHOLD,
                actor -> fn.test(actor) ? actor : null));
    }

    /**
     * Determine whether or not this map has an item on the given map point
     * @param mapPoint a non-null map point
     * @return {@code true} if it has an item on the given map point, {@code false} otherwise
     */
    public boolean hasItemOn(WorldMapPoint mapPoint) {
        return items.containsKey(mapPoint);
    }

    /**
     * Determine whether or not a map point is available
     * @param mapPoint a non-null map point
     * @return {@code true} if the given map point is available, {@code false} otherwise
     */
    public boolean isAvailable(WorldMapPoint mapPoint) {
        return !hasActorOn(mapPoint) && !hasItemOn(mapPoint);
    }

    /**
     * Return the first available same or adjacent map point
     * @param mapPoint a non-null map point
     * @return an optional map point
     */
    public Optional<WorldMapPoint> findFirstAvailableAdjacent(WorldMapPoint mapPoint) {
        return Stream.concat(Stream.of(mapPoint), mapPoint.adjacents(true))
                .filter(this::isAvailable)
                .findAny();
    }

    /**
     * Determine whether or not you can add an item
     * @param mapPoint a non-null map point
     * @param exact if {@code false} will search first available adjacents cells
     * @return {@code true} if you can add an item, {@code false} otherwise
     */
    public boolean canAddItem(WorldMapPoint mapPoint, boolean exact) {
        return exact
                ? isAvailable(mapPoint)
                : findFirstAvailableAdjacent(mapPoint).isPresent();
    }

    /**
     * Try to add an item on the map
     * @param itemSupplier a non-null supplier
     * @param mapPoint a non-null map point
     * @param exact if {@code false} will search first available adjacents cells
     * @return {@code true} there was enough room, {@code false} otherwise
     * @throws java.lang.IllegalArgumentException if item already has been added
     */
    public boolean tryAddItem(Supplier<WorldItem> itemSupplier, WorldMapPoint mapPoint, boolean exact) {
        requireNonNull(itemSupplier, "itemSupplier");
        requireNonNull(mapPoint, "mapPoint");

        WorldItem item;

        synchronized (items) {
            if (!exact) {
                Optional<WorldMapPoint> option = findFirstAvailableAdjacent(mapPoint);

                if (!option.isPresent()) {
                    return false;
                }

                mapPoint = option.get(); // bad, but what the hell?
            } else if (!isAvailable(mapPoint)) {
                return false;
            }

            item = itemSupplier.get();
            items.put(mapPoint, item);
        }

        eventBus.publish(new MapItemAddEvent(this, item, mapPoint));
        return true;
    }

    /**
     * Try to add an item on the map on the exact given map point
     * @param itemSupplier a non-null item
     * @param mapPoint a non-null map point
     * @return {@code true} there was enough room, {@code false} otherwise
     * @throws java.lang.IllegalArgumentException if item already has been added
     */
    public boolean tryAddItem(Supplier<WorldItem> itemSupplier, WorldMapPoint mapPoint) {
        return tryAddItem(itemSupplier, mapPoint, true);
    }

    /**
     * Remove an item on the map given a map point
     * @param mapPoint a non-null map point
     * @return an optional item
     */
    public Optional<WorldItem> tryRemoveItem(WorldMapPoint mapPoint) {
        requireNonNull(mapPoint, "mapPoint");

        synchronized (items) {
            WorldItem item;
            item = items.remove(mapPoint);
            if (item == null) {
                return Optional.empty();
            }
            eventBus.publish(new MapItemRemoveEvent(this, item, mapPoint));
            return Optional.of(item);
        }
    }

    public Optional<DirectionsEnum> tryOrientationTo(WorldMap other) {
        if (super.getTop() == other.getId()) {
            return Optional.of(DIRECTION_NORTH);
        } else if (super.getRight() == other.getId()) {
            return Optional.of(DIRECTION_EAST);
        } else if (super.getBottom() == other.getId()) {
            return Optional.of(DIRECTION_SOUTH);
        } else if (super.getLeft() == other.getId()) {
            return Optional.of(DIRECTION_WEST);
        }

        return Optional.empty();
    }

    public boolean isAdjacentTo(WorldMap other) {
        return tryOrientationTo(other).isPresent();
    }

    public DirectionsEnum orientationTo(WorldMap other) {
        return tryOrientationTo(other)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "map %d isn't adjacent to %d",
                        this.getId(), other.getId())))
                ;
    }

    @Override
    public String toString() {
        return "WorldMap(" +
                "id=" + getId() +
                ", nr-actors=" + actors.size() +
                ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(super.getId());
    }
}
