package org.heat.world.roleplay;

import org.heat.shared.LightweightException;

public class WorldActionCanceledException extends LightweightException {
    public WorldActionCanceledException() {
        super("action has been canceled");
    }

}
