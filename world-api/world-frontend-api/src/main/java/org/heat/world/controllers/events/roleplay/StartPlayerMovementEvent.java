package org.heat.world.controllers.events.roleplay;

import org.heat.world.roleplay.environment.WorldMovement;

public final class StartPlayerMovementEvent {
    private final WorldMovement movement;

    public StartPlayerMovementEvent(WorldMovement movement) {
        this.movement = movement;
    }

    public WorldMovement getMovement() {
        return movement;
    }
}
