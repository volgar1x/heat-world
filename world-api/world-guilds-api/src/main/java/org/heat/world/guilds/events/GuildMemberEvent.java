package org.heat.world.guilds.events;

import org.heat.world.guilds.WorldGuild;
import org.heat.world.guilds.WorldGuildMember;

public abstract class GuildMemberEvent extends GuildEvent {
    private final WorldGuildMember member;

    protected GuildMemberEvent(WorldGuild guild, WorldGuildMember member) {
        super(guild);
        this.member = member;
    }

    public WorldGuildMember getMember() {
        return member;
    }
}
