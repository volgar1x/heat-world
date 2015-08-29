package org.heat.world.controllers.events;

import com.ankamagames.dofus.network.enums.GameContextEnum;

/**
 * This event is published when quitting a context.
 */
public final class QuitContextEvent {
    private final GameContextEnum context;

    public QuitContextEvent(GameContextEnum context) {
        this.context = context;
    }

    public GameContextEnum getContext() {
        return context;
    }
}
