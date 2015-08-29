package org.heat.world.controllers.events;

import com.ankamagames.dofus.network.enums.GameContextEnum;

/**
 * This event represents the actual creation of a new context
 */
public final class CreateContextEvent {
    private final GameContextEnum context;

    public CreateContextEvent(GameContextEnum context) {
        this.context = context;
    }

    public GameContextEnum getContext() {
        return context;
    }
}
