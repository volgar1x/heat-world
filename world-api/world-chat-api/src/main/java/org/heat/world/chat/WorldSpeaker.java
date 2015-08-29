package org.heat.world.chat;

import org.heat.UserRank;
import org.heat.world.roleplay.WorldActor;

public interface WorldSpeaker extends WorldActor {
    /**
     * Get speaker id
     * @return an int
     */
    int getSpeakerId();

    /**
     * Get speaker user id
     * @return an int
     */
    int getSpeakerUserId();

    /**
     * Get speaker name
     * @return a non-null {@link java.lang.String}
     */
    String getSpeakerName();

    /**
     * Get speaker rank
     * @return a non-null {@link UserRank}
     */
    UserRank getSpeakerRank();
}
