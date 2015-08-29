package org.heat.world.roleplay.environment;

import com.ankamagames.dofus.network.enums.DirectionsEnum;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a specific point of a map
 */
public final class WorldMapPoint {
    public static final int MAP_WIDTH = 14;
    public static final int MAP_HEIGHT = 20;
    public static final int CELL_COUNT = MAP_HEIGHT * MAP_WIDTH * 2;

    public final byte x;
    public final byte y;
    public final short cellId;

    private WorldMapPoint(byte x, byte y, short cellId) {
        this.x = x;
        this.y = y;
        this.cellId = cellId;
    }

    public Optional<WorldMapPoint> tryPlusX(int x) {
        return of(this.x + x, y);
    }

    public Optional<WorldMapPoint> tryPlusY(int y) {
        return of(x, this.y + y);
    }

    public Optional<WorldMapPoint> tryPlusCellId(int cellId) {
        return of(this.cellId + cellId);
    }

    public WorldMapPoint plusX(int x) {
        return tryPlusX(x).get();
    }

    public WorldMapPoint plusY(int y) {
        return tryPlusY(y).get();
    }

    public WorldMapPoint plusCellId(int cellId) {
        return tryPlusCellId(cellId).get();
    }

    /**
     * https://github.com/Emudofus/Dofus/blob/2.19.2-beta.84001.1/com/ankamagames/jerakine/types/positions/MapPoint.as#L183
     */
    public DirectionsEnum orientationTo(WorldMapPoint other, boolean fourDir) {
        int ac = other.x - this.x;
        int bc = this.y - other.y;

        double angle = Math.acos(ac / Math.sqrt(Math.pow(ac, 2) + Math.pow(bc, 2))) * 180 / Math.PI * (other.y > this.y ? -1 : 1);

        int dir;
        if(fourDir) {
            dir = (int) Math.round(angle / 90.0) * 2 + 1;
        } else {
            dir = (int) Math.round(angle / 45.0) + 1;
        }

        if (dir < 0) {
            dir += 8;
        }

        return DirectionsEnum.valueOf((byte) dir).get();
    }

    public DirectionsEnum ortientationTo(WorldMapPoint other) {
        return orientationTo(other, true);
    }

    /**
     * https://github.com/Emudofus/Dofus/blob/2.19.2-beta.84001.1/com/ankamagames/jerakine/types/positions/MapPoint.as#L221
     */
    public Optional<WorldMapPoint> applyDirection(DirectionsEnum dir) {
        switch (dir) {
            case DIRECTION_EAST:       return of(x + 1, y + 1);
            case DIRECTION_SOUTH_EAST: return of(x + 1, y    );
            case DIRECTION_SOUTH:      return of(x + 1, y - 1);
            case DIRECTION_SOUTH_WEST: return of(x    , y - 1);
            case DIRECTION_WEST:       return of(x - 1, y - 1);
            case DIRECTION_NORTH_WEST: return of(x - 1, y    );
            case DIRECTION_NORTH:      return of(x - 1, y + 1);
            case DIRECTION_NORTH_EAST: return of(x    , y + 1);
            default: throw new Error();
        }
    }

    public Stream<WorldMapPoint> adjacents(boolean fourDir) {
        Stream.Builder<WorldMapPoint> res = Stream.builder();

        of(x + 1, y    ).ifPresent(res); // DIRECTION_SOUTH_EAST
        of(x    , y - 1).ifPresent(res); // DIRECTION_SOUTH_WEST
        of(x - 1, y    ).ifPresent(res); // DIRECTION_NORTH_WEST
        of(x    , y + 1).ifPresent(res); // DIRECTION_NORTH_EAST

        if (!fourDir) {
            of(x + 1, y + 1).ifPresent(res); // DIRECTION_EAST
            of(x + 1, y - 1).ifPresent(res); // DIRECTION_SOUTH
            of(x - 1, y - 1).ifPresent(res); // DIRECTION_WEST
            of(x - 1, y + 1).ifPresent(res); // DIRECTION_NORTH
        }

        return res.build();
    }

    public Stream<WorldMapPoint> adjacents() {
        return adjacents(false);
    }

    public boolean isAdjacentTo(WorldMapPoint other) {
        return Math.abs(this.x - other.x) == 1 ||
                Math.abs(this.y - other.y) == 1;
    }

    private static class Cache {
        private static final WorldMapPoint[] BY_CELL_ID = new WorldMapPoint[CELL_COUNT];

        static {
            int startX = 0, startY = 0, cell = 0;
            for (int a = 0; a < MAP_HEIGHT; a++) {
                for (int b = 0; b < MAP_WIDTH; b++) {
                    int x = startX + b, y = startY + b;
                    WorldMapPoint point = new WorldMapPoint((byte) x, (byte) y, (short) cell);
                    BY_CELL_ID[cell] = point;
                    cell++;
                }
                startX++;

                for (int b = 0; b < MAP_WIDTH; b++) {
                    int x = startX + b, y = startY + b;
                    WorldMapPoint point = new WorldMapPoint((byte) x, (byte) y, (short) cell);
                    BY_CELL_ID[cell] = point;
                    cell++;
                }
                startY--;
            }
        }
    }

    public static boolean isValidCoord(int x, int y) {
        return (x + y >= 0) && (x - y >= 0) && (x - y < MAP_HEIGHT * 2) && (x + y < MAP_WIDTH * 2);
    }

    public static Optional<WorldMapPoint> of(int x, int y) {
        if (!isValidCoord(x, y)) {
            return Optional.empty();
        }
        return of((x - y) * MAP_WIDTH + y + (x - y) / 2);
    }

    public static WorldMapPoint get(int x, int y) {
        return of(x, y).get();
    }

    public static Optional<WorldMapPoint> of(int cellId) {
        if (cellId < 0 || cellId > CELL_COUNT) {
            return Optional.empty();
        }
        return Optional.of(Cache.BY_CELL_ID[cellId]);
    }

    public static WorldMapPoint get(int cellId) {
        return of(cellId).get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldMapPoint that = (WorldMapPoint) o;

        return cellId == that.cellId;
    }

    @Override
    public int hashCode() {
        return cellId;
    }

    @Override
    public String toString() {
        return "WorldMapPoint(" +
                "x=" + x +
                ", y=" + y +
                ", cellId=" + cellId +
                ')';
    }
}
