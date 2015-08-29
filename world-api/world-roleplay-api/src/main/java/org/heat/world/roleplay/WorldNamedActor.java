package org.heat.world.roleplay;

import com.ankamagames.dofus.network.types.game.context.roleplay.GameRolePlayNamedActorInformations;

public interface WorldNamedActor extends WorldActor {
    String getActorName();

    @Override
    GameRolePlayNamedActorInformations toGameRolePlayActorInformations();
}
