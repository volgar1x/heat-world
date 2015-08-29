package org.heat.world.guilds;

import com.ankamagames.dofus.network.types.game.guild.GuildEmblem;
import org.heat.shared.LightweightException;

public interface WorldGuildFactory {
    WorldGuild create(WorldGuildMember leader, String name, GuildEmblem emblem);

    abstract class Err extends LightweightException {
        protected Err(String message) {
            super(message);
        }
    }
    class InvalidNameErr extends Err {
        public InvalidNameErr() {
            super("Invalid Guild name");
        }
    }
    class ExistingNameErr extends Err {
        public ExistingNameErr() {
            super("Already existing Guild name");
        }
    }
    class InvalidEmblemErr extends Err {
        public InvalidEmblemErr() {
            super("Invalid Guild emblem");
        }
    }
}
