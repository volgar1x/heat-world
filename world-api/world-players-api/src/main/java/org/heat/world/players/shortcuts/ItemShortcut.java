package org.heat.world.players.shortcuts;

import com.ankamagames.dofus.network.enums.ShortcutBarEnum;
import com.ankamagames.dofus.network.types.game.shortcut.Shortcut;
import com.ankamagames.dofus.network.types.game.shortcut.ShortcutObjectItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true, of = {"itemUid", "itemGid"})
public final class ItemShortcut extends PlayerShortcut {
    public static final ShortcutBarEnum BAR_TYPE = ShortcutBarEnum.GENERAL_SHORTCUT_BAR;

    @Getter private final int itemUid;
    @Getter private final int itemGid;

    public ItemShortcut(int playerId, int slot, int itemUid, int itemGid) {
        super(playerId, slot);
        this.itemUid = itemUid;
        this.itemGid = itemGid;
    }

    @Override
    public ShortcutBarEnum getBarType() {
        return BAR_TYPE;
    }

    @Override
    public Shortcut toShortcut() {
        return new ShortcutObjectItem(getSlot(), getItemUid(), getItemGid());
    }

    @Override
    protected PlayerShortcut copyShortcut(int playerId, int slot) {
        return new ItemShortcut(playerId, slot, itemUid, itemGid);
    }

    @Override
    public String toString() {
        return "ItemShortcut(" +
                "player_id=" + getPlayerId() +
                ", slot=" + getSlot() +
                ", itemUid=" + itemUid +
                ", itemGid=" + itemGid +
                ')';
    }
}
