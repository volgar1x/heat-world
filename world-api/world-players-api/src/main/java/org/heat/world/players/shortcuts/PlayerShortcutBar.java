package org.heat.world.players.shortcuts;

import com.ankamagames.dofus.network.enums.ShortcutBarEnum;
import org.fungsi.Either;
import org.heat.shared.Pair;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface PlayerShortcutBar {
    /**
     * Get all shortcuts on a given bar type
     * @param bar a non-null shortcut bar type
     * @return a non-null, non-leaking stream
     */
    Stream<PlayerShortcut> getShortcutsOf(ShortcutBarEnum bar);

    /**
     * Find a shortcut on a given bar type and a given slot
     * @param bar a non-null shortcut bar type
     * @param slot a valid integer slot
     * @return a non-null option
     */
    Optional<PlayerShortcut> findShortcut(ShortcutBarEnum bar, int slot);

    /**
     * Add a shortcut to this bar
     * @param shortcut a non-null shortcut
     * @return {@code true} if it has been added, {@code false} if slot was already taken
     */
    boolean add(PlayerShortcut shortcut);

    /**
     * Remove a shortcut from this bar
     * @param bar a non-null shortcut bar type
     * @param slot a valid integer slot
     * @return {@code true} if it has been removed, {@code false} if there was no shortcut on given slot
     */
    boolean remove(ShortcutBarEnum bar, int slot);

    /**
     * Swap a shortcut on this bar
     * @param bar a non-null shortcut bar type
     * @param from a valid integer slot
     * @param to a valid integer slot
     * @return either the moved shortcut or the two swapped shortcuts
     * @throws java.util.NoSuchElementException if there is no shortcut on "from" slot
     */
    Either<PlayerShortcut, Pair<PlayerShortcut, PlayerShortcut>> swap(ShortcutBarEnum bar, int from, int to);

    /**
     * Remove an item shortcut given its uid
     * @param itemUid a valid integer item uid
     * @return a non-null list of removed shortcuts
     */
    List<PlayerShortcut> removeItemShortcut(int itemUid);
}
