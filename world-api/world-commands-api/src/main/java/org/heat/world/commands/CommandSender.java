package org.heat.world.commands;

import com.github.blackrush.acara.EventBus;
import org.heat.UserRank;

/**
 * Managed by romain on 08/03/2015.
 */
public interface CommandSender {
    EventBus getEventBus();
    void reply(boolean error, boolean console, Object... text);
    UserRank getRank();
}
