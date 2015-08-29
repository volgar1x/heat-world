package org.heat.world.roleplay;

import com.ankamagames.dofus.network.types.game.look.EntityLook;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Wither;
import org.heat.world.items.WorldItem;

import java.util.stream.Stream;

@Getter
@Wither
@EqualsAndHashCode
@ToString
public final class WorldActorLook {
    final short bones;
    final short[] skins;
    final short[] scales;
    final int[] colors;

    public WorldActorLook(short bones, short[] skins, short[] scales, int[] colors) {
        this.bones = bones;
        this.skins = skins;
        this.scales = scales;
        this.colors = colors;
    }

    public static WorldActorLook create(short bones, short lookId, short headId, short scale, int[] colors, Stream<WorldItem> displayedItems) {
        short[] skins = computeSkins(lookId, headId, displayedItems);
        return new WorldActorLook(bones, skins, new short[]{scale}, WorldActorLooks.toIndexedColors(colors));
    }

    private static short[] computeSkins(int lookId, int headId, Stream<WorldItem> items) {
        WorldItem[] arr = items.toArray(WorldItem[]::new);
        short[] skins = new short[2 + arr.length];

        skins[0] = (short) lookId;
        skins[1] = (short) headId;

        for (int i = 0; i < arr.length; i++) {
            skins[2 + i] = (short) arr[i].getTemplate().appearanceId;
        }

        return skins;
    }

    public int getLookId() {
        if (skins.length <= 0) {
            throw new IllegalStateException();
        }
        return skins[0];
    }

    public int getHeadId() {
        if (skins.length <= 1) {
            throw new IllegalStateException();
        }
        return skins[1];
    }

    public int getScale() {
        if (scales.length != 1) {
            throw new IllegalStateException();
        }
        return scales[0];
    }

    public WorldActorLook withItems(Stream<WorldItem> items) {
        return withSkins(computeSkins(getLookId(), getHeadId(), items));
    }

    public EntityLook toEntityLook() {
        return new EntityLook(
                bones,
                skins,
                colors,
                scales,
                Stream.empty() // TODO(world): entity look subentities
        );
    }

    public static final short
        STANDING_BONES = 1,
        MOUNTING_BONES = 639
        ;
}
