package org.heat.world.controllers.events.roleplay;

import org.heat.world.items.WorldItem;

public final class EquipItemEvent {
    private final WorldItem item;
    private final boolean apply;

    public EquipItemEvent(WorldItem item, boolean apply) {
        this.item = item;
        this.apply = apply;
    }

    public WorldItem getItem() {
        return item;
    }

    public boolean isApply() {
        return apply;
    }
}
