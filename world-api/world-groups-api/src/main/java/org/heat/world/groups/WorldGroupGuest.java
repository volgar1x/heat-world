package org.heat.world.groups;

import com.ankamagames.dofus.network.types.game.context.roleplay.party.PartyGuestInformations;
import com.ankamagames.dofus.network.types.game.context.roleplay.party.companion.PartyCompanionBaseInformations;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
@Getter
public final class WorldGroupGuest {
    final WorldGroupMember guest;
    final WorldGroupMember inviter;
    final Instant invitedAt;

    public PartyGuestInformations toPartyGuestInformations() {
        return new PartyGuestInformations(
                guest.getActorId(),
                inviter.getActorId(),
                guest.getActorName(),
                guest.getActorLook().toEntityLook(),
                guest.getActorBreed().value,
                guest.getActorSex(),
                guest.toPlayerStatus(),
                new PartyCompanionBaseInformations[0]
        );
    }
}
