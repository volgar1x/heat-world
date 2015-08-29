package org.heat.world.guilds.events;

import org.heat.world.guilds.WorldGuild;
import org.heat.world.guilds.WorldGuildMember;

public class KickedGuildMemberEvent extends GuildMemberEvent {
    private final WorldGuildMember kicker;

    public KickedGuildMemberEvent(WorldGuild guild, WorldGuildMember member, WorldGuildMember kicker) {
        super(guild, member);
        this.kicker = kicker;
    }

    public WorldGuildMember getKicker() {
        return kicker;
    }

    public boolean isKicked() {
        return getMember() != getKicker();
    }

    public boolean isLeaving() {
        return !isKicked();
    }
}
