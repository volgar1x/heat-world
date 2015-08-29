package org.heat.world.guilds.events;

import org.heat.world.guilds.WorldGuild;
import org.heat.world.guilds.WorldGuildMember;
import org.rocket.Nullable;

public final class NewGuildMemberEvent extends GuildMemberEvent {
    private final WorldGuildMember recruter;

    public NewGuildMemberEvent(WorldGuild guild, WorldGuildMember member, @Nullable WorldGuildMember recruter) {
        super(guild, member);
        this.recruter = recruter;
    }

    public @Nullable WorldGuildMember getRecruter() {return recruter;}
}
