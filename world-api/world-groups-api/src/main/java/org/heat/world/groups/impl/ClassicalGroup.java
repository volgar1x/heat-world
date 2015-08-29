package org.heat.world.groups.impl;

import com.ankamagames.dofus.network.enums.ChatActivableChannelsEnum;
import com.ankamagames.dofus.network.enums.PartyTypeEnum;
import com.github.blackrush.acara.EventBus;
import lombok.Getter;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Promise;
import org.fungsi.concurrent.Promises;
import org.heat.world.chat.WorldSpeaker;
import org.heat.world.groups.*;
import org.heat.world.groups.events.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

final class ClassicalGroup implements WorldGroup {
    @Getter final int groupId;
    @Getter final EventBus eventBus;
    @Getter final int maxMembers;

    WorldGroupMember leader;
    String name = "";
    final Map<Integer, WorldGroupMember> members = new HashMap<>();
    final Map<Integer, Invit> invitations = new HashMap<>();

    ClassicalGroup(int groupId, EventBus eventBus, int maxMembers, WorldGroupMember leader) {
        this.groupId = groupId;
        this.eventBus = eventBus;
        this.maxMembers = maxMembers;
        this.leader = leader;
        this.members.put(leader.getActorId(), leader);
    }

    void disband() {
        invitations.forEach((guestId, invit) -> invit.notifyCancellation(leader));
        invitations.clear();
        members.clear();
        leader = null;
        eventBus.publish(new DisbandGroupEvent(this));
    }

    void disbandIfNeeded() {
        if (leader != null && members.size() <= 1) {
            disband();
        }
    }

    void addMember(WorldGroupMember member) {
        if (members.size() >= maxMembers) {
            throw new WorldGroupMemberOverflowException();
        }
        members.put(member.getActorId(), member);
    }

    void removeMember(WorldGroupMember member) {
        if (!members.remove(member.getActorId(), member)) {
            throw new IllegalArgumentException();
        }
    }

    void hasMember(WorldGroupMember member) {
        if (!members.containsKey(member.getActorId())) {
            throw new IllegalArgumentException();
        }
    }

    void hasNotMember(WorldGroupMember member) {
        if (members.containsKey(member.getActorId())) {
            throw new IllegalArgumentException();
        }
    }

    void notDisbanded() {
        if (leader == null) {
            throw new IllegalStateException();
        }
    }

    @Override
    public PartyTypeEnum getGroupType() {
        return PartyTypeEnum.PARTY_TYPE_CLASSICAL;
    }

    @Override
    public int getChannelId() {
        return ChatActivableChannelsEnum.CHANNEL_PARTY.value;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean accepts(WorldSpeaker speaker) {
        return members.containsValue(speaker);
    }

    @Override
    public WorldGroupMember getLeader() {
        notDisbanded();
        return leader;
    }

    @Override
    public void abdicateLeader(WorldGroupMember newLeader) {
        notDisbanded();
        hasMember(newLeader);
//        WorldGroupMember oldLeader = this.leader;
        this.leader = newLeader;
        eventBus.publish(new AbdicateGroupEvent(this, newLeader));
    }

    @Override
    public String getGroupName() {
        return name;
    }

    @Override
    public void setGroupName(String name) {
        this.name = name;
        eventBus.publish(new NewNameGroupEvent(this, name));
    }

    @Override
    public Stream<WorldGroupMember> getMemberStream() {
        notDisbanded();
        return members.values().stream();
    }

    @Override
    public Stream<WorldGroupGuest> getGuestStream() {
        notDisbanded();
        return invitations.values().stream().map(Invit::getGroupGuest);
    }

    @Override
    public Optional<WorldGroupMember> findMember(int memberId) {
        notDisbanded();
        return Optional.ofNullable(members.get(memberId));
    }

    @Override
    public Invitation invite(WorldGroupMember inviter, WorldGroupMember guest) {
        notDisbanded();
        hasMember(inviter);
        hasNotMember(guest);
        return new Invit(new WorldGroupGuest(guest, inviter, Instant.now()));
    }

    @Override
    public Optional<Invitation> findInvitation(int guestId) {
        notDisbanded();
        return Optional.ofNullable(invitations.get(guestId));
    }

    @Override
    public void update(WorldGroupMember member) {
        notDisbanded();
        hasMember(member);
        eventBus.publish(new UpdateGroupMemberEvent(this, member));
    }

    @Override
    public void leave(WorldGroupMember member) {
        notDisbanded();
        if (leader == member) {
            disband();
        } else {
            removeMember(member);
            eventBus.publish(new LeaveGroupMemberEvent(this, member));
            disbandIfNeeded();
        }
    }

    @Override
    public void kick(WorldGroupMember kicker, WorldGroupMember member) {
        notDisbanded();
        removeMember(member);
        eventBus.publish(new KickGroupMemberEvent(this, member, kicker));
        disbandIfNeeded();
    }

    class Invit implements Invitation {
        @Getter final WorldGroupGuest groupGuest;
        final Promise<Unit> end = Promises.create();

        Invit(WorldGroupGuest groupGuest) {
            this.groupGuest = groupGuest;
            invitations.put(groupGuest.getGuest().getActorId(), this);
            eventBus.publish(new NewGuestGroupEvent(ClassicalGroup.this, groupGuest));
        }

        @Override
        public WorldGroup getGroup() {
            return ClassicalGroup.this;
        }

        @Override
        public Future<Unit> getInvitationEndFuture() {
            return end;
        }

        @Override
        public void accept() {
            WorldGroupMember guest = groupGuest.getGuest();
            invitations.remove(guest.getActorId());
            addMember(guest);
            eventBus.publish(new NewGroupMemberEvent(ClassicalGroup.this, guest));
        }

        @Override
        public void refuse() {
            invitations.remove(groupGuest.getGuest().getActorId());
            eventBus.publish(new RemoveGuestGroupEvent(ClassicalGroup.this, groupGuest));
        }

        @Override
        public void cancel(WorldGroupMember canceller) {
            hasMember(canceller);
            invitations.remove(groupGuest.getGuest().getActorId());
            notifyCancellation(canceller);
        }

        void notifyCancellation(WorldGroupMember canceller) {
            eventBus.publish(new CancelGuestGroupEvent(ClassicalGroup.this, groupGuest, canceller));
            end.fail(new WorldGroupInvitationCancelledException(canceller));
        }
    }
}
