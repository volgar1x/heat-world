package org.heat.world.guilds.impl;

import com.ankamagames.dofus.network.enums.ChatActivableChannelsEnum;
import com.ankamagames.dofus.network.types.game.context.roleplay.BasicGuildInformations;
import com.ankamagames.dofus.network.types.game.context.roleplay.GuildInformations;
import com.ankamagames.dofus.network.types.game.guild.GuildEmblem;
import com.ankamagames.dofus.network.types.game.guild.GuildMember;
import com.github.blackrush.acara.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.heat.world.chat.WorldSpeaker;
import org.heat.world.guilds.*;
import org.heat.world.guilds.events.KickedGuildMemberEvent;
import org.heat.world.guilds.events.NewGuildMemberEvent;
import org.heat.world.guilds.events.RankGuildMemberEvent;
import org.rocket.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.ankamagames.dofus.network.enums.GuildRightsBitEnum.*;

@Slf4j
final class ClassicalGuild implements WorldGuild {
    private final EventBus eventBus;
    private final WorldGuildMemberRepository memberRepository;
    private final byte initialGivenExperiencePercent;

    private final int         id;
    private final String      name;
    private final GuildEmblem emblem;

    private final Map<Integer, WorldGuildMember> members = new HashMap<>();

    ClassicalGuild(EventBus eventBus, WorldGuildMemberRepository memberRepository, byte initialGivenExperiencePercent,
                   int id, String name, GuildEmblem emblem) {
        this.eventBus = eventBus;
        this.memberRepository = memberRepository;
        this.initialGivenExperiencePercent = initialGivenExperiencePercent;
        this.id = id;
        this.name = name;
        this.emblem = emblem;
    }

    void checkin(WorldGuildMember member) {
        members.put(member.getActorId(), member);
    }

    void checkin(List<? extends WorldGuildMember> members) {
        members.forEach(this::checkin);
    }

    void checkout(WorldGuildMember member) {
        members.remove(member.getActorId());
    }

    void add(WorldGuildMember member, @Nullable WorldGuildMember recruter) {
        member.setGuildRank(WorldGuildRanks.ON_TRIAL.id);
        member.setGuildPermissions(WorldGuildPermissions.NONE);
        member.setGuildGivenExperiencePercent(initialGivenExperiencePercent);

        memberRepository.save(member)
                        .onFailure(err -> log.error("cannot add member to guild", err))
                        .onSuccess(u -> {
                            checkin(member);
                            eventBus.publish(new NewGuildMemberEvent(this, member, recruter));
                        });
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GuildEmblem getEmblem() {
        return emblem;
    }

    @Override
    public int getChannelId() {
        return ChatActivableChannelsEnum.CHANNEL_GUILD.value;
    }

    @Override
    public boolean accepts(WorldSpeaker speaker) {
        return members.containsKey(speaker.getActorId());
    }

    @Override
    public @Nullable Invitation invite(WorldGuildMember recruter, WorldGuildMember recruted) {
        return new Invit(recruter, recruted);
    }

    @Override
    public @Nullable WorldGuildMember findMember(int id) {
        return members.get(id);
    }

    @Override
    public @Nullable WorldGuildMember update(WorldGuildMember updater, int memberId, short rank, byte givenExperiencePercent,
                          WorldGuildPermissions perms) {
        WorldGuildMember member = findMember(memberId);
        if (member == null) {
            return null;
        }

        boolean updated = false;

        if (updater.getGuildPermissions().has(GUILD_RIGHT_MANAGE_RANKS)) {
            short oldRank = member.getGuildRank();
            member.setGuildRank(rank);
            eventBus.publish(new RankGuildMemberEvent(this, member, oldRank));
            updated = true;
        }

        if (updater.getGuildPermissions().has(GUILD_RIGHT_MANAGE_XP_CONTRIBUTION) ||
            updater.getGuildPermissions().has(GUILD_RIGHT_MANAGE_MY_XP_CONTRIBUTION) && member == updater) {
            member.setGuildGivenExperiencePercent(givenExperiencePercent);
            updated = true;
        }

        if (updater.getGuildPermissions().has(GUILD_RIGHT_MANAGE_RIGHTS)) {
            member.setGuildPermissions(perms);
            updated = true;
        }

        if (updated) {
            // do not wait for repository's response
            memberRepository.save(member);
            return member;
        }

        return null;
    }

    @Override
    public void kick(WorldGuildMember kicker, int kickedId) {
        WorldGuildMember kicked = findMember(kickedId);
        if (kicked == null) {
            return;
        }

        if (kicked.getGuildRank() == WorldGuildRanks.LEADER.id) {
            return;
        }

        if (!kicker.getGuildPermissions().has(GUILD_RIGHT_BAN_MEMBERS) && kicker != kicked) {
            return;
        }

        checkout(kicked);

        memberRepository.remove(kicked).onSuccess(x -> eventBus.publish(
                new KickedGuildMemberEvent(this, kicked, kicker)));
    }

    @Override
    public int getNrMembersLimit() {
        return 9999;
    }

    @Override
    public BasicGuildInformations toBasicGuildInformations() {
        return new BasicGuildInformations(id, name);
    }

    @Override
    public GuildInformations toGuildInformations() {
        return new GuildInformations(id, name, emblem);
    }

    @Override
    public Stream<GuildMember> toGuildMember() {
        return members.values().stream()
                      .map(WorldGuildMember::toGuildMember);
    }

    class Invit extends Invitation {
        public Invit(WorldGuildMember recruter, WorldGuildMember recruted) {super(recruter, recruted);}

        @Override public WorldGuild getGuild() {return ClassicalGuild.this;}

        @Override
        protected void onAccepted() {
            add(getRecruted(), getRecruter());
        }
    }
}
