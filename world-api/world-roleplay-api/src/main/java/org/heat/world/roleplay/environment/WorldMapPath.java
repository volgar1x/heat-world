package org.heat.world.roleplay.environment;

import com.ankamagames.dofus.network.enums.DirectionsEnum;
import com.google.common.collect.ImmutableList;
import lombok.Value;
import lombok.experimental.Wither;

import static java.util.Objects.requireNonNull;

@Value
public final class WorldMapPath {
    @Value
    @Wither
    public static class Node {
        final WorldMapPoint point;
        final DirectionsEnum dir;

        public static Node parse(short key) {
            WorldMapPoint pos = WorldMapPoint.of(key & 0xFFF).get();
            DirectionsEnum dir = DirectionsEnum.valueOf((byte) ((key >> 12) & 0xF)).get();
            return new Node(pos, dir);
        }

        public short export() {
            return (short) ((dir.value << 12) | point.cellId & 0xFFF);
        }

        /**
         * Apply a direction to this node and allocate a new node
         * @return a non-null node
         */
        public Node next() {
            return withPoint(point.applyDirection(dir).get());
        }
    }

    final WorldPosition position;
    final ImmutableList<Node> nodes;

    /**
     * Parse given array of keys on a given position
     * @param position a non-null, resolved position
     * @param keys a non-null array of 16-bits integers
     * @return a non-null path that might not be valid depending where path takes place
     * @throws java.lang.IllegalArgumentException if given position is not resolved or given array of keys is not valid
     */
    public static WorldMapPath parse(WorldPosition position, short[] keys) {
        if (!position.isResolved()) {
            throw new IllegalArgumentException("must have a resolved position");
        }
        if (keys.length == 0) {
            return new WorldMapPath(position, ImmutableList.of());
        }
        if (keys.length == 1) {
            throw new IllegalArgumentException("must have zero or at least two keys");
        }

        ImmutableList.Builder<Node> nodes = ImmutableList.builder();

        for (int i = 0; i < keys.length - 1; i++) {
            Node a = Node.parse(keys[i]), b = Node.parse(keys[i + 1]);

            for (Node it = a; !it.point.equals(b.point); it = it.next()) {
                nodes.add(it);
            }
        }

        nodes.add(Node.parse(keys[keys.length - 1]));

        return new WorldMapPath(position, nodes.build());
    }

    /**
     * Export path as an array of keys
     * @return a non-null array of 16-bits integers
     */
    public short[] export() {
        short[] res = new short[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            res[i] = node.export();
        }

        return res;
    }

    /**
     * Check validity of path
     * @param fourDirs if true, it checks only on four directions
     * @return a boolean
     */
    @SuppressWarnings("UnusedParameters")
    public boolean isValid(boolean fourDirs) {
        // TODO(world-api): validity of a path
        return true;
    }

    /**
     * Check validity of path on all directions
     * @return a boolean
     */
    public boolean isValid() {
        return isValid(false);
    }

    /**
     * Get first node of path
     * @return a non-null node
     * @throws java.lang.IllegalStateException if path is empty
     */
    public Node origin() {
        if (nodes.isEmpty()) {
            throw new IllegalStateException("this path is empty");
        }
        return nodes.get(0);
    }

    /**
     * Get last node of path
     * @return a non-null node
     * @throws java.lang.IllegalStateException if path is empty
     */
    public Node target() {
        if (nodes.isEmpty()) {
            throw new IllegalStateException("this path is empty");
        }
        return nodes.get(nodes.size() - 1);
    }

    /**
     * Check whether or not path contains given {@link org.heat.world.roleplay.environment.WorldMapPoint}
     * @param point a non-null point
     * @return a boolean
     * @throws java.lang.NullPointerException if given point is null
     */
    public boolean contains(WorldMapPoint point) {
        requireNonNull(point, "point");
        return nodes.stream().map(Node::getPoint).anyMatch(point::equals);
    }
}
