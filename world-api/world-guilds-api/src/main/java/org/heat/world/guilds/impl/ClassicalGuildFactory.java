package org.heat.world.guilds.impl;

import com.ankamagames.dofus.network.types.game.guild.GuildEmblem;
import com.github.blackrush.acara.EventBus;
import com.typesafe.config.Config;
import org.heat.world.guilds.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClassicalGuildFactory implements WorldGuildFactory {
    private final Provider<EventBus>         eventBusProvider;
    private final WorldGuildMemberRepository memberRepository;
    private final byte                       initialGivenExperiencePercent;

    private final AtomicInteger idGenerator = new AtomicInteger();

    @Inject
    public ClassicalGuildFactory(Config config, Provider<EventBus> eventBusProvider, WorldGuildRepository repo,
                                 WorldGuildMemberRepository memberRepository) {
        this.eventBusProvider = eventBusProvider;
        this.memberRepository = memberRepository;
        this.initialGivenExperiencePercent = (byte) config.getInt("heat.world.guilds.initial-given-experience-percent");
        this.idGenerator.set(repo.getLastId() + 1);
    }

    @Override
    public WorldGuild create(WorldGuildMember leader, String name, GuildEmblem emblem) {
        leader.setGuildPermissions(WorldGuildPermissions.ALL);
        leader.setGuildRank(WorldGuildRanks.LEADER.id);
        leader.setGuildGivenExperiencePercent(initialGivenExperiencePercent);

        ClassicalGuild guild = new ClassicalGuild(eventBusProvider.get(), memberRepository,
                                                  initialGivenExperiencePercent, idGenerator.getAndIncrement(),
                                                  name, emblem);

        guild.checkin(leader);

        return guild;
    }
}
