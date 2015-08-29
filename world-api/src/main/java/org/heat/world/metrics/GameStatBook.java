package org.heat.world.metrics;

import com.google.common.collect.ImmutableMap;

import java.util.Optional;

public interface GameStatBook {
    <T extends GameStat> Optional<T> lookup(GameStats<T> id);

    GameStatBook copy();
    ImmutableMap<GameStats<?>, GameStat> copyAsMap();

    default <T extends GameStat> T get(GameStats<T> id) {
        return lookup(id).get();
    }
}
