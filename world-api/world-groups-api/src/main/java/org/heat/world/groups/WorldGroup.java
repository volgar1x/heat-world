package org.heat.world.groups;

import com.ankamagames.dofus.network.enums.PartyTypeEnum;
import com.ankamagames.dofus.network.types.game.context.roleplay.party.PartyGuestInformations;
import com.ankamagames.dofus.network.types.game.context.roleplay.party.PartyInvitationMemberInformations;
import com.ankamagames.dofus.network.types.game.context.roleplay.party.PartyMemberInformations;
import com.github.blackrush.acara.EventBus;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.heat.world.chat.VirtualWorldChannel;
import org.heat.world.roleplay.WorldHumanoidActor;

import java.util.Optional;
import java.util.stream.Stream;

public interface WorldGroup extends VirtualWorldChannel {
    EventBus getEventBus();

    int getGroupId();
    PartyTypeEnum getGroupType();
    String getGroupName();
    void setGroupName(String name);

    WorldGroupMember getLeader();
    void abdicateLeader(WorldGroupMember newLeader);

    int getMaxMembers();

    Invitation invite(WorldGroupMember inviter, WorldGroupMember guest);
    Optional<Invitation> findInvitation(int guestId);
    void update(WorldGroupMember member);
    void leave(WorldGroupMember member);
    void kick(WorldGroupMember kicker, WorldGroupMember member);

    Stream<WorldGroupMember> getMemberStream();
    Optional<WorldGroupMember> findMember(int memberId);

    Stream<WorldGroupGuest> getGuestStream();

    default Stream<PartyMemberInformations> toPartyMemberInformations() {
        return getMemberStream().map(WorldGroupMember::toPartyMemberInformations);
    }

    default Stream<PartyInvitationMemberInformations> toPartyInvitationMemberInformations() {
        return getMemberStream().map(WorldGroupMember::toPartyInvitationMemberInformations);
    }

    default Stream<PartyGuestInformations> toPartyGuestInformations() {
        return getGuestStream().map(WorldGroupGuest::toPartyGuestInformations);
    }

    public interface Invitation {
        Future<Unit> getInvitationEndFuture();
        WorldGroup getGroup();
        WorldGroupGuest getGroupGuest();

        void accept();
        void refuse();
        void cancel(WorldGroupMember canceller);

        default WorldHumanoidActor getGuest() {
            return getGroupGuest().getGuest();
        }

        default WorldGroupMember getInviter() {
            return getGroupGuest().getInviter();
        }
    }
}
