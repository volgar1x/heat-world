package org.heat.world.trading;

import org.heat.world.items.WorldBag;
import org.heat.world.roleplay.WorldNamedActor;

public interface TraderInterface extends WorldNamedActor {
    WorldBag getTraderBag();
}
