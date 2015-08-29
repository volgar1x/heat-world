package org.heat.world.items;

import com.ankamagames.dofus.datacenter.items.Item;
import com.ankamagames.dofus.datacenter.items.Weapon;
import com.ankamagames.dofus.network.enums.CharacterInventoryPositionEnum;
import com.ankamagames.dofus.network.types.game.data.items.ObjectItem;
import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fungsi.Either;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * {@link org.heat.world.items.WorldItem} is an immutable data class.
 *
 * <p>
 * It has two identifiers :
 * <ol>
 *     <li>UID, standing for User IDentifier, a unique identifier across all items</li>
 *     <li>GID, standing for Group IDentifier, an identifier referencing template's id</li>
 * </ol>
 */
@Getter
@RequiredArgsConstructor(staticName = "create")
@EqualsAndHashCode(of = {"uid", "version"})
public final class WorldItem {
    final int uid;
    final long version;
    final Item template;
    final ImmutableSet<WorldItemEffect> effects;
    final CharacterInventoryPositionEnum position;
    final int quantity;

    // backdoors
    WorldItem copy(Item template, ImmutableSet<WorldItemEffect> effects, CharacterInventoryPositionEnum position, int quantity) {
        return create(uid, version + 1, template, effects, position, quantity);
    }

    // mutators
    public WorldItem withUid(int uid) {
        return create(uid, version + 1, template, effects, position, quantity);
    }

    public WorldItem withNewVersion() {
        return copy(template, effects, position, quantity);
    }

    public WorldItem withPosition(CharacterInventoryPositionEnum position) {
        requireNonNull(position, "position");
        return copy(template, effects, position, quantity);
    }

    public WorldItem withQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be positive or zero");
        }
        return copy(template, effects, position, quantity);
    }

    public WorldItem withEffects(ImmutableSet<WorldItemEffect> effects) {
        requireNonNull(effects, "effects");
        return copy(template, effects, position, quantity);
    }

    public WorldItem fork(int quantity) {
        return create(0, 0, template, effects, position, quantity);
    }

    // shorthands
    public int getGid() {
        return getTemplate().getId();
    }

    public WorldItemType getItemType() {
        return WorldItemType.valueOf((byte) getTemplate().getTypeId());
    }

    public boolean isWeapon() {
        return getTemplate() instanceof Weapon;
    }

    public boolean isMapDisplayable() {
        return position == CharacterInventoryPositionEnum.ACCESSORY_POSITION_HAT ||
                position == CharacterInventoryPositionEnum.ACCESSORY_POSITION_CAPE ||
                position == CharacterInventoryPositionEnum.ACCESSORY_POSITION_SHIELD ||
                position == CharacterInventoryPositionEnum.ACCESSORY_POSITION_WEAPON;
    }

    public Either<Item, Weapon> getItemOrWeapon() {
        return isWeapon() ? Either.right(((Weapon) getTemplate())) : Either.left(getTemplate());
    }

    public WorldItem plusQuantity(int quantity) {
        return withQuantity(getQuantity() + quantity);
    }

    public WorldItem mapEffects(Function<WorldItemEffect, WorldItemEffect> fn) {
        ImmutableSet.Builder<WorldItemEffect> newEffects = ImmutableSet.builder();
        for (WorldItemEffect effect : getEffects()) {
            newEffects.add(fn.apply(effect));
        }

        return withEffects(newEffects.build());
    }

    public ObjectItem toObjectItem() {
        return new ObjectItem(
                getPosition().value,
                (short) getGid(),
                getEffects().stream().map(WorldItemEffect::toObjectEffect),
                getUid(),
                getQuantity()
        );
    }

    public static int compare(WorldItem left, WorldItem right) {
        if (left.getTemplate().getId() != right.getTemplate().getId()) {
            return left.getTemplate().getId() - right.getTemplate().getId();
        }

        // take advantage of set characteristics to compare effects
        ImmutableSet<WorldItemEffect> rightEffects = right.getEffects();
        ImmutableSet<WorldItemEffect> leftEffects = left.getEffects();

        for (WorldItemEffect effect : rightEffects) {
            if (!leftEffects.contains(effect)) {
                return -1;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        return "WorldItem{" +
                "uid=" + uid +
                ", gid=" + template.getId() +
                ", version=" + version +
                ", effects=" + effects +
                ", position=" + position +
                ", quantity=" + quantity +
                '}';
    }
}
