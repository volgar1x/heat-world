package org.heat.world.chat;

import com.github.blackrush.acara.Subscribable;

public interface WorldChannel {
    /**
     * Get channel id
     * @return a valid channel id
     */
    int getChannelId();

    /**
     * Get a subscribable view of this channel
     * @return a non-null subscribable channel view
     */
    Subscribable getSubscribableChannelView();

    /**
     * Make a {@code WorldSpeaker} speak on the channel
     * @param speaker a non-null speaker
     * @param message a non-null message
     */
    void speak(WorldSpeaker speaker, WorldChannelMessage message);

    boolean accepts(WorldSpeaker speaker);
}
