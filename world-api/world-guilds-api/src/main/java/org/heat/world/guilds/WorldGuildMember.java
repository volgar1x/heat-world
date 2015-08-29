package org.heat.world.guilds;

import com.ankamagames.dofus.network.types.game.guild.GuildMember;
import org.heat.world.chat.WorldSpeaker;
import org.heat.world.roleplay.WorldHumanoidActor;

public interface WorldGuildMember extends WorldHumanoidActor, WorldSpeaker {
    WorldGuildPermissions getGuildPermissions();
    void setGuildPermissions(WorldGuildPermissions permissions);

    short getGuildRank();
    void setGuildRank(short rank);

    byte getGuildGivenExperiencePercent();
    void setGuildGivenExperiencePercent(byte givenExperiencePercent);

    GuildMember toGuildMember();
}
