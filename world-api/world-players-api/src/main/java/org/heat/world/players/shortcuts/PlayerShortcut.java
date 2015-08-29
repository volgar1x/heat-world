package org.heat.world.players.shortcuts;

import com.ankamagames.dofus.network.enums.ShortcutBarEnum;
import com.ankamagames.dofus.network.types.game.shortcut.Shortcut;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = {"playerId", "slot"})
public abstract class PlayerShortcut {
    @Getter private final int playerId;
    @Getter private final int slot;

    protected PlayerShortcut(int playerId, int slot) {
        this.playerId = playerId;
        this.slot = slot;
    }

    public abstract ShortcutBarEnum getBarType();

    public abstract Shortcut toShortcut();

    protected abstract PlayerShortcut copyShortcut(int playerId, int slot);

    public PlayerShortcut withPlayerId(int playerId) {
        return copyShortcut(playerId, slot);
    }

    public PlayerShortcut withSlot(int slot) {
        return copyShortcut(playerId, slot);
    }
}
