package org.heat.world.players;

import org.fungsi.concurrent.Future;
import org.heat.world.users.WorldUser;

public interface PlayerFactory {
    Future<Player> create(WorldUser user, String name, byte breed, boolean sex, int[] colors, int cosmeticId);
}
