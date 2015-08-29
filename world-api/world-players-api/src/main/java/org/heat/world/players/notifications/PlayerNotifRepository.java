package org.heat.world.players.notifications;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;

/**
 * Managed by romain on 07/04/2015.
 */
public interface PlayerNotifRepository {
    Future<int[]> findAll(int playerId);
    Future<Unit> save(int playerId, int notification);
    Future<Unit> removeAll(int playerId);
}