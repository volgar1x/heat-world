package org.heat.world.controllers.events;

import com.ankamagames.dofus.network.enums.GameContextEnum;

/**
 * This event represents the end of creation of a context
 */
public final class EnterContextEvent {
    private final GameContextEnum context;

    public EnterContextEvent(GameContextEnum context) {
        this.context = context;
    }

    public GameContextEnum getContext() {
        return context;
    }
}
