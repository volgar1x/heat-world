package org.heat.world.players.items;

import org.heat.world.items.WorldItemWallet;

public interface PlayerItemWallet extends WorldItemWallet {
    WorldItemWallet createTemp();
}
