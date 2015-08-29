package org.heat.world.items;

import com.ankamagames.dofus.datacenter.effects.EffectInstance;
import com.ankamagames.dofus.datacenter.items.Item;
import com.ankamagames.dofus.datacenter.items.Weapon;
import com.google.common.collect.ImmutableSet;
import org.heat.datacenter.Datacenter;

import javax.inject.Inject;

import static com.ankamagames.dofus.network.enums.CharacterInventoryPositionEnum.INVENTORY_POSITION_NOT_EQUIPED;

public final class DefaultItemFactory implements WorldItemFactory {
    private final Datacenter datacenter;

    @Inject
    public DefaultItemFactory(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    @Override
    public WorldItem create(int templateId, int quantity) {
        // TODO: datacenter polymorphic fetch
        Item template = datacenter.find(Item.class, templateId)
                .orElseGet(() -> datacenter.find(Weapon.class, templateId).get());

        return WorldItem.create(0, 0, template, createEffects(template), INVENTORY_POSITION_NOT_EQUIPED, quantity);
    }

    ImmutableSet<WorldItemEffect> createEffects(Item item) {
        ImmutableSet.Builder<WorldItemEffect> effects = ImmutableSet.builder();
        for (EffectInstance e : item.getPossibleEffects()) {
            effects.add(Effects.fromEffectInstance(e));
        }
        return effects.build();
    }
}
