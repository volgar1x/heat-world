package org.heat.world.players;

import com.ankamagames.dofus.network.enums.CharacterCreationResultEnum;
import org.heat.shared.LightweightException;

public class PlayerCreationException extends LightweightException {
    private final CharacterCreationResultEnum reason;

    public PlayerCreationException(CharacterCreationResultEnum reason) {
        super(reason.name());
        this.reason = reason;
    }

    public CharacterCreationResultEnum getReason() {
        return reason;
    }
}
