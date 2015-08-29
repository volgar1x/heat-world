package org.heat.world.players.metrics;

import com.ankamagames.dofus.datacenter.spells.Spell;
import com.ankamagames.dofus.datacenter.spells.SpellLevel;
import com.ankamagames.dofus.network.types.game.data.items.SpellItem;
import org.heat.datacenter.Datacenter;

public final class PlayerSpell {
    public static final short NO_POSITION = (short) 63;

    private final Datacenter datacenter;
    private final Spell spellData;

    private SpellLevel levelData;
    private byte level;
    private short position;

    public PlayerSpell(Datacenter datacenter, Spell spellData, SpellLevel levelData, byte level, short position) {
        this.datacenter = datacenter;
        this.spellData = spellData;
        this.levelData = levelData;
        this.level = level;
        this.position = position;
    }

    public static PlayerSpell of(Datacenter datacenter, int spellId, byte level, short position) {
        Spell spellData = datacenter.find(Spell.class, spellId).get();
        SpellLevel levelData = datacenter.find(SpellLevel.class, (int) spellData.getSpellLevels()[0]).get();
        return new PlayerSpell(datacenter, spellData, levelData, level, position);
    }

    public int getId() {
        return spellData.getId();
    }

    public Spell getSpellData() {
        return spellData;
    }

    public int getMinPlayerLevel() {
        return levelData.getMinPlayerLevel();
    }

    public SpellLevel getLevelData() {
        return levelData;
    }

    public short getPosition() {
        return position;
    }

    public void setPosition(short position) {
        this.position = position;
    }

    public boolean hasPosition() {
        return position != NO_POSITION;
    }

    public boolean hasPosition(int position) {
        return this.position == position;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
        levelData = datacenter.find(SpellLevel.class, (int) spellData.getSpellLevels()[level - 1])
                .orElseThrow(() -> new RuntimeException(String.format(
                        "spell_id=%d level_id=%d level=%d",
                        spellData.getIconId(), spellData.getSpellLevels()[0], level)));
    }

    public void setLevel(int level) {
        if (level > Byte.MAX_VALUE || level < Byte.MAX_VALUE) {
            throw new ArithmeticException();
        }
        setLevel((byte) level);
    }

    public void resetLevel() {
        setLevel((byte) 0);
    }

    public void plusLevel(int level) {
        setLevel(getLevel() + level);
    }

    public SpellItem toSpellItem() {
        return new SpellItem(position, spellData.getId(), level);
    }

    @Override
    public int hashCode() {
        return spellData.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        PlayerSpell other = (PlayerSpell) obj;
        return this.spellData.getId() == other.spellData.getId()
                && this.levelData.getId() == other.levelData.getId()
                && this.position == other.position;
    }

    @Override
    public String toString() {
        return "PlayerSpell(" +
                "id=" + getId() + ", " +
                "level=" + getLevel() + ", " +
                "position=" + (hasPosition() ? position : "none") +
                ")";
    }
}
