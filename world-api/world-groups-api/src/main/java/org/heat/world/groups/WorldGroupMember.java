package org.heat.world.groups;

import com.ankamagames.dofus.network.types.game.context.roleplay.party.PartyInvitationMemberInformations;
import com.ankamagames.dofus.network.types.game.context.roleplay.party.PartyMemberInformations;
import org.heat.world.roleplay.WorldHumanoidActor;

public interface WorldGroupMember extends WorldHumanoidActor {
    PartyMemberInformations toPartyMemberInformations();
    PartyInvitationMemberInformations toPartyInvitationMemberInformations();
}
