package org.heat.world.chat;

import com.ankamagames.dofus.network.enums.ChatChannelsMultiEnum;
import com.github.blackrush.acara.EventBus;
import org.heat.world.roleplay.environment.WorldMap;

public class MapChannel implements VirtualWorldChannel {
    private final WorldMap map;

    public MapChannel(WorldMap map) {
        this.map = map;
    }

    @Override
    public EventBus getEventBus() {
        return map.getEventBus();
    }

    @Override
    public int getChannelId() {
        return ChatChannelsMultiEnum.CHANNEL_GLOBAL.value;
    }

    @Override
    public boolean accepts(WorldSpeaker speaker) {
        return map.hasActor(speaker);
    }
}
