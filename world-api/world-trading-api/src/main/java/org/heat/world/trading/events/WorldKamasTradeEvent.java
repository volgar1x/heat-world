package org.heat.world.trading.events;

import org.heat.world.trading.WorldKamasTrade;
import org.heat.world.trading.WorldKamasTrader;

public abstract class WorldKamasTradeEvent extends WorldTradeEvent {
    private final int kamas;

    protected WorldKamasTradeEvent(int kamas) {
        this.kamas = kamas;
    }

    @Override
    public abstract WorldKamasTrade getTrade();

    public abstract WorldKamasTrader getTrader();

    public int getKamas() {
        return kamas;
    }
}
