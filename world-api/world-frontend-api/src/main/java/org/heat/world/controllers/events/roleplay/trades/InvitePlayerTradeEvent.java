package org.heat.world.controllers.events.roleplay.trades;

import org.heat.shared.LightweightException;
import org.heat.world.players.Player;
import org.heat.world.trading.impl.player.PlayerTrade;

public final class InvitePlayerTradeEvent {
    private final PlayerTrade trade;
    private final Player source;

    public InvitePlayerTradeEvent(PlayerTrade trade, Player source) {
        this.trade = trade;
        this.source = source;
    }

    public PlayerTrade getTrade() {
        return trade;
    }

    public Player getSource() {
        return source;
    }

    public enum AckT { INSTANCE }
    public static final AckT Ack = AckT.INSTANCE;

    public static final class Busy extends LightweightException {
        public Busy() {
            super("i am busy right now, i can't trade with you");
        }
    }
}
