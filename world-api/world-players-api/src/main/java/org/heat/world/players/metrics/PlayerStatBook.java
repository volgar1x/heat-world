package org.heat.world.players.metrics;

import org.heat.world.items.WorldItem;
import org.heat.world.metrics.GameStatBook;
import org.heat.world.metrics.GameStats;
import org.heat.world.metrics.RegularStat;

import java.util.stream.Stream;

public interface PlayerStatBook extends GameStatBook {
    int getStatsPoints();
    void plusStatsPoints(int statsPoints);

    int getSpellsPoints();
    void plusSpellsPoints(int spellsPoints);

    /**
     * Upgrade a stat by a given amount of points. This also remove <i>statsPoints</i> from this book
     * @param id a non-null stat
     * @param points a positive integer
     * @return amount of points added
     */
    int upgradeStat(GameStats<RegularStat> id, int points);

    /**
     * Apply item's effects to this stat book
     * @param item a non-null item
     */
    void apply(WorldItem item);

    /**
     * Unapply item's effects from this stat book
     * @param item a non-null item
     */
    void unapply(WorldItem item);

    /**
     * {@inheritDoc}
     */
    @Override
    PlayerStatBook copy();

    /**
     * Apply one by one items' effects to this stat book
     * @param items a non-null, non-leaking stream
     */
    default void apply(Stream<WorldItem> items) {
        items.forEach(this::apply);
    }

    /**
     * Unapply one by one items' effects to this stat book
     * @param items a non-null, non-leaking stream
     */
    default void unapply(Stream<WorldItem> items) {
        items.forEach(this::unapply);
    }
}
