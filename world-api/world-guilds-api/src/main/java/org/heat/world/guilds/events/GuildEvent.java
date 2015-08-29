package org.heat.world.guilds.events;

import org.heat.world.guilds.WorldGuild;

public abstract class GuildEvent {
    private final WorldGuild guild;

    protected GuildEvent(WorldGuild guild) {this.guild = guild;}

    public WorldGuild getGuild() {
        return guild;
    }
}
