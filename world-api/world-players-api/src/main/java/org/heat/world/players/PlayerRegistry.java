package org.heat.world.players;

import com.ankamagames.dofus.network.types.game.character.status.PlayerStatus;
import com.github.blackrush.acara.EventBus;
import org.heat.world.users.WorldUser;
import org.rocket.Nullable;

/**
 * <p>
 * {@link org.heat.world.players.PlayerRegistry} contains all currently online players
 *
 * <p>
 * Be careful, a player found from is only online <b>right now</b>, it may soon go offline even if you still have
 * a reference to it. Use a timeout when publishing events on its bus to automatically fail if it goes offline.
 */
public interface PlayerRegistry {
    /**
     * Event bus where you can listen {@link org.heat.world.players.events.OnlinePlayerEvent}
     * and {@link org.heat.world.players.events.OfflinePlayerEvent}
     * @return a non-null event bus
     */
    EventBus getEventBus();

    /**
     * Find an online player by its id
     * @param id an integer representing player's id
     * @return an optional player
     */
    @Nullable Player findPlayer(int id);

    /**
     * Find an online player by its user id
     * @param userId an integer representing {@link org.heat.User#getId()}
     * @return an optional player
     */
    @Nullable Player findPlayerByUserId(int userId);

    /**
     * Find an online player by its name
     * @param name a string representing player's name
     * @return an optional player
     */
    @Nullable Player findPlayerByName(String name);

    /**
     * Determine whether or not one of {@link org.heat.User}'s players is registered
     * @param userId an integer representing {@link org.heat.User#getId()}
     * @return {@code true} if a user is registered, {@code false} otherwise
     */
    default boolean isUserRegisted(int userId) {
        return findPlayerByUserId(userId) != null;
    }

    /**
     * Find an online player by its user.
     * @param user player's user
     * @return an optional player
     */
    default @Nullable Player findPlayerByUser(WorldUser user) {
        return findPlayerByUserId(user.getId());
    }

    /**
     * Get the current status of a player. Never returns {@code null}
     * @param id player's ID
     * @return never {@code null}
     */
    default PlayerStatus getPlayerStatusById(int id) {
        Player player = findPlayer(id);

        if (player != null) {
            return player.getStatus();
        }

        return Players.OFFLINE_STATUS;
    }

    /**
     * Add a player to this registry in order to appear online
     * @param player a non-null player
     * @throws org.heat.world.players.PlayerAlreadyRegisteredException if it already contains the given player or
     * @throws org.heat.world.players.UserAlreadyRegisteredException if its one of its user's already is
     */
    void add(Player player);

    /**
     * Remove a plyer from this registry in order to appear offline
     * @param player a non-null player
     */
    void remove(Player player);
}
