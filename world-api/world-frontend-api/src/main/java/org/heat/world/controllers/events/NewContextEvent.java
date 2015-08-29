package org.heat.world.controllers.events;

import com.ankamagames.dofus.network.enums.GameContextEnum;

/**
 * This event represents the willing to create a new context
 */
public final class NewContextEvent {
    private final GameContextEnum context;

    public NewContextEvent(GameContextEnum context) {
        this.context = context;
    }

    public GameContextEnum getContext() {
        return context;
    }
}
