package org.heat.world.roleplay.environment;

import com.google.common.collect.ImmutableList;
import org.heat.shared.Pair;
import org.junit.Test;

import static org.heat.world.roleplay.environment.WorldMapPoint.get;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorldMapPointTest {
    @Test
    public void testIsAdjacent() throws Exception {
        // given
        ImmutableList<Pair<WorldMapPoint, WorldMapPoint>> points =
                ImmutableList.of(
                        Pair.of(get(0, 0), get(1, 1)),
                        Pair.of(get(0, 0), get(1, 0)),
                        Pair.of(get(0, 0), get(1, -1))
                );

        // when
        // then
        for (Pair<WorldMapPoint, WorldMapPoint> pair : points) {
            assertTrue(pair.first + " is adjacent to " + pair.second, pair.first.isAdjacentTo(pair.second));
        }
    }

    @Test
    public void testIsNotAdjacent() throws Exception {
        // given
        ImmutableList<Pair<WorldMapPoint, WorldMapPoint>> points =
                ImmutableList.of(
                        Pair.of(get(0, 0), get(2, 0)),
                        Pair.of(get(0, 0), get(18, -5))
                );

        // when
        // then
        for (Pair<WorldMapPoint, WorldMapPoint> pair : points) {
            assertFalse(pair.first + " is adjacent to " + pair.second, pair.first.isAdjacentTo(pair.second));
        }
    }
}