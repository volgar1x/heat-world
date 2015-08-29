package org.heat.world.controllers.events;

import com.ankamagames.dofus.network.enums.GameContextEnum;

/**
 * This event is published when destroying a context. (eg. switch from roleplay to fight, or when disconnecting)
 */
public final class DestroyContextEvent {
    private final GameContextEnum context;

    public DestroyContextEvent(GameContextEnum context) {
        this.context = context;
    }

    public GameContextEnum getContext() {
        return context;
    }
}
