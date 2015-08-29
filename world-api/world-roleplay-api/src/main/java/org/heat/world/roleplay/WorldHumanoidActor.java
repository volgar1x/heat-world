package org.heat.world.roleplay;

import com.ankamagames.dofus.network.enums.BreedEnum;
import com.ankamagames.dofus.network.types.game.character.restriction.ActorRestrictionsInformations;
import com.ankamagames.dofus.network.types.game.character.status.PlayerStatus;
import com.ankamagames.dofus.network.types.game.context.roleplay.GameRolePlayHumanoidInformations;
import com.ankamagames.dofus.network.types.game.context.roleplay.HumanInformations;

public interface WorldHumanoidActor extends WorldNamedActor {
    int getActorUserId();
    boolean getActorSex();
    BreedEnum getActorBreed();

    PlayerStatus toPlayerStatus();
    ActorRestrictionsInformations toActorRestrictionsInformations();
    HumanInformations toHumanInformations();

    @Override
    GameRolePlayHumanoidInformations toGameRolePlayActorInformations();
}
