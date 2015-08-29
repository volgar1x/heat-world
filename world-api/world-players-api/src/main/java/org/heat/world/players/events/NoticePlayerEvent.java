package org.heat.world.players.events;

import com.ankamagames.dofus.network.enums.TextInformationTypeEnum;
import lombok.Getter;

/**
 * Managed by romain on 08/03/2015.
 */

@Getter
public final class NoticePlayerEvent {
    //TextInformationMessage
    private final int imId;
    private final TextInformationTypeEnum type;
    private final Object[] args;

    public NoticePlayerEvent(int imId, TextInformationTypeEnum type, Object... args) {
        this.imId = imId;
        this.type = type;
        this.args = args;
    }
}
