package org.heat.world.controllers.utils;

import com.ankamagames.dofus.network.messages.game.basic.BasicNoOperationMessage;
import com.ankamagames.dofus.network.messages.game.basic.BasicTimeMessage;

import java.time.OffsetDateTime;

public final class Basics {
    private Basics() {}

    public static BasicTimeMessage time(OffsetDateTime dateTime) {
        return new BasicTimeMessage(dateTime.toEpochSecond(), (short) dateTime.getOffset().getTotalSeconds());
    }

    public static BasicTimeMessage time() {
        return time(OffsetDateTime.now());
    }

    public static BasicNoOperationMessage noop() {
        return BasicNoOperationMessage.i;
    }
}
