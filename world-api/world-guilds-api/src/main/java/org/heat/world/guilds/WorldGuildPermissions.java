package org.heat.world.guilds;

import com.ankamagames.dofus.network.enums.GuildRightsBitEnum;

public final class WorldGuildPermissions {
    private final int bits;

    private WorldGuildPermissions(int bits) {
        this.bits = bits;
    }

    public static final WorldGuildPermissions NONE = new WorldGuildPermissions(0),
                                              ALL = new WorldGuildPermissions(-1);

    public static WorldGuildPermissions of(int bits) {
        switch (bits) {
            case 0: return NONE;
            case -1: return ALL;
            default: return new WorldGuildPermissions(bits);
        }
    }

    public int getBits() {
        return bits;
    }

    public boolean has(GuildRightsBitEnum e) {
        return (this.bits & e.value) != 0;
    }

    public WorldGuildPermissions with(GuildRightsBitEnum e) {
        return of(this.bits | e.value);
    }

    public WorldGuildPermissions without(GuildRightsBitEnum e) {
        return of(this.bits ^ e.value);
    }

    @Override
    public int hashCode() {
        return bits;
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return ((WorldGuildPermissions) obj).bits == this.bits;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WorldGuildPermissions(");
        switch (bits) {
            case 0: sb.append("NONE"); break;

            case -1: sb.append("ALL"); break;

            default:
                boolean first = true;
                for (GuildRightsBitEnum bit : GuildRightsBitEnum.values()) {
                    if (!has(bit)) {
                        continue;
                    }
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(bit.name());
                }
        }
        sb.append(')');
        return sb.toString();
    }
}
