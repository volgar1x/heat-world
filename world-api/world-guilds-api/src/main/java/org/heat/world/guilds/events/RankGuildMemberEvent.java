package org.heat.world.guilds.events;

import org.heat.world.guilds.WorldGuild;
import org.heat.world.guilds.WorldGuildMember;

public final class RankGuildMemberEvent extends GuildMemberEvent {
    private final short oldRank;

    public RankGuildMemberEvent(WorldGuild guild, WorldGuildMember member, short oldRank) {
        super(guild, member);
        this.oldRank = oldRank;
    }

    public short getOldRank() {
        return oldRank;
    }
}
