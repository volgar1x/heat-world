package org.heat.world.controllers.events.roleplay;

import org.heat.world.roleplay.environment.WorldMovement;

public final class EndPlayerMovementEvent {
    private final WorldMovement movement;

    public EndPlayerMovementEvent(WorldMovement movement) {
        this.movement = movement;
    }

    public WorldMovement getMovement() {
        return movement;
    }
}
