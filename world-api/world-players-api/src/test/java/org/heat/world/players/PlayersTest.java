package org.heat.world.players;

import lombok.RequiredArgsConstructor;
import org.heat.shared.IntPair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlayersTest {

    @Test
    public void testGetCostOneUpgrade() throws Exception {
        // given
        final long[][] stats = new long[][] {
                { 0, 1 },
                { 100, 2 },
                { 200, 3 },
                { 300, 4 },
                { 400, 5 },
        };

        final int[][] expectations = new int[][] {
        // actual, cost
            {0, 1},
            {99, 1},
            {100, 2},
            {199, 2},
            {200, 3},
            {299, 3},
            {300, 4},
            {399, 4},
            {400, 5},
            {499, 5},
            {1000, 5},
        };

        // when
        // then
        for (int[] expectation : expectations) {
            assertEquals("cost of one upgrade when actual=" + expectation[0],
                    expectation[1],
                    Players.getCostOneUpgrade(stats, expectation[0])
            );
        }
    }

    @RequiredArgsConstructor(staticName = "$")
    static final class expectation {
        final int actual;
        final int points;
        final IntPair expected;
        final long[][] stats;
    }

    @Test
    public void testUpgrade() throws Exception {
        // given
        final long[][] stats = new long[][] {
                { 0, 1 },
                { 100, 2 },
                { 200, 3 },
                { 300, 4 },
                { 400, 5 },
        };

        final expectation[] expectations = new expectation[]{
                expectation.$(0, 200, IntPair.of(150, 200), stats),
                expectation.$(100, 100, IntPair.of(50, 100), stats),
                expectation.$(200, 100, IntPair.of(33, 99), stats),
                expectation.$(0, 100, IntPair.of(100, 100), stats),
                expectation.$(0, 101, IntPair.of(100, 100), stats),
                expectation.$(0, 102, IntPair.of(101, 102), stats),
                expectation.$(100, 1, IntPair.of(0, 0), stats),
                expectation.$(100, 2, IntPair.of(1, 2), stats),
                expectation.$(200, 2, IntPair.of(0, 0), stats),
                expectation.$(0, 100, IntPair.of(50, 100), new long[][] {
                        { 0, 2 },
                        { 50, 3 },
                        { 100, 4 },
                        { 150, 5 },
                }),
        };

        // when
        // then
        for (expectation expectation : expectations) {
            assertEquals(
                    "upgrade(actual=" + expectation.actual + ", points=" + expectation.points + ")",
                    expectation.expected,
                    Players.upgrade(expectation.stats, expectation.actual, expectation.points)
            );
        }
    }
}