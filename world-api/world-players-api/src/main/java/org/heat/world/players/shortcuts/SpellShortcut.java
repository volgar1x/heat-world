package org.heat.world.players.shortcuts;

import com.ankamagames.dofus.network.enums.ShortcutBarEnum;
import com.ankamagames.dofus.network.types.game.shortcut.Shortcut;
import com.ankamagames.dofus.network.types.game.shortcut.ShortcutSpell;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true, of = "spellId")
public final class SpellShortcut extends PlayerShortcut {
    public static final ShortcutBarEnum BAR_TYPE = ShortcutBarEnum.SPELL_SHORTCUT_BAR;

    @Getter private final short spellId;

    public SpellShortcut(int playerId, int slot, short spellId) {
        super(playerId, slot);
        this.spellId = spellId;
    }

    @Override
    public ShortcutBarEnum getBarType() {
        return BAR_TYPE;
    }

    @Override
    public Shortcut toShortcut() {
        return new ShortcutSpell(getSlot(), spellId);
    }

    @Override
    protected PlayerShortcut copyShortcut(int playerId, int slot) {
        return new SpellShortcut(playerId, slot, spellId);
    }

    @Override
    public String toString() {
        return "SpellShortcut(" +
                "player_id=" + getPlayerId() +
                ", slot=" + getSlot() +
                ", spellId=" + spellId +
                ')';
    }
}
