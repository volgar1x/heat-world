package org.heat.world.roleplay;

import com.ankamagames.dofus.network.types.game.context.roleplay.GameRolePlayActorInformations;
import org.heat.world.roleplay.environment.WorldPosition;

public interface WorldActor {
    int getActorId();
    WorldActorLook getActorLook();
    WorldPosition getActorPosition();

    GameRolePlayActorInformations toGameRolePlayActorInformations();
}
