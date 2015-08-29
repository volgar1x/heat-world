package org.heat.world.items;

import com.ankamagames.dofus.network.enums.CharacterInventoryPositionEnum;

import java.util.NoSuchElementException;

public enum WorldItemType {
    AMULET((byte) 1),
    BOW((byte) 2),
    WAND((byte) 3),
    STAFF((byte) 4),
    DAGGER((byte) 5),
    SWORD((byte) 6),
    HAMMER((byte) 7),
    SHOVEL((byte) 8),
    RING((byte) 9),
    BELT((byte) 10),
    BOOTS((byte) 11),
    POTION((byte) 12),
    EXPERIENCE_SCROLL((byte) 13),
    ORDER_ABILITY_ITEM((byte) 14),
    MISCELLANEOUS((byte) 15),
    HAT((byte) 16),
    CLOAK((byte) 17),
    PET((byte) 18),
    AXE((byte) 19),
    TOOL((byte) 20),
    PICKAXE((byte) 21),
    SCYTHE((byte) 22),
    DOFUS((byte) 23),
    MISCELLANEOUS_CONT((byte) 24),
    DOCUMENT((byte) 25),
    SMITHMAGIC_POTION((byte) 26),
    MUTATION_ITEM((byte) 27),
    BOOST_FOOD((byte) 28),
    BLESSING((byte) 29),
    CURSE((byte) 30),
    ROLEPLAY_BUFF((byte) 31),
    FOLLOWING_CHARACTER((byte) 32),
    BREAD((byte) 33),
    CEREAL((byte) 34),
    FLOWER((byte) 35),
    PLANT((byte) 36),
    BEER((byte) 37),
    WOOD((byte) 38),
    ORE((byte) 39),
    ALLOY((byte) 40),
    FISH((byte) 41),
    TREAT((byte) 42),
    SPELL_DELEVELING_POTION((byte) 43),
    PROFESSION_DELEVELING_POTION((byte) 44),
    WEAPON_SKILL_DELEVELING_POTION((byte) 45),
    FRUIT((byte) 46),
    BONE((byte) 47),
    POWDER((byte) 48),
    EDIBLE_FISH((byte) 49),
    PRECIOUS_STONE((byte) 50),
    STONE((byte) 51),
    FLOUR((byte) 52),
    FEATHER((byte) 53),
    HAIR((byte) 54),
    FABRIC((byte) 55),
    LEATHER((byte) 56),
    WOOL((byte) 57),
    SEED((byte) 58),
    SKIN((byte) 59),
    OIL((byte) 60),
    STUFFED_TOY((byte) 61),
    GUTTED_FISH((byte) 62),
    MEAT((byte) 63),
    PRESERVED_MEAT((byte) 64),
    TAIL((byte) 65),
    METARIA((byte) 66),
    VEGETABLE((byte) 68),
    EDIBLE_MEAT((byte) 69),
    DYE((byte) 70),
    ALCHEMY_EQUIPMENT((byte) 71),
    PET_EGG((byte) 72),
    SKILL((byte) 73),
    FAIRYWORK((byte) 74),
    SPELL_LEARNING_SCROLL((byte) 75),
    CHARACTERISTIC_SCROLL((byte) 76),
    PET_CERTIFICATE((byte) 77),
    SMITHMAGIC_RUNE((byte) 78),
    DRINK((byte) 79),
    MISSION_ITEM((byte) 80),
    BACKPACK((byte) 81),
    SHIELD((byte) 82),
    SOUL_STONE((byte) 83),
    KEY((byte) 84),
    FULL_SOUL_STONE((byte) 85),
    PERCEPTOR_DELEVELING_POTION((byte) 86),
    SEEKER_SCROLL((byte) 87),
    MAGIC_STONE((byte) 88),
    GIFT((byte) 89),
    PET_GHOST((byte) 90),
    DRAGOTURKEY((byte) 91),
    GOBBALL((byte) 92),
    BREEDING_ITEM((byte) 93),
    VARIOUS((byte) 94),
    PLANK((byte) 95),
    BARK((byte) 96),
    MOUNT_CERTIFICATE((byte) 97),
    ROOT((byte) 98),
    CAPTURING_NET((byte) 99),
    BAG_OF_RESOURCES((byte) 100),
    CROSSBOW((byte) 102),
    LEG((byte) 103),
    WING((byte) 104),
    EGG((byte) 105),
    EAR((byte) 106),
    CARAPACE((byte) 107),
    BUD((byte) 108),
    EYE((byte) 109),
    JELLY((byte) 110),
    SHELL((byte) 111),
    PRISM((byte) 112),
    LIVING_OBJECT((byte) 113),
    MAGIC_WEAPON((byte) 114),
    FRAGMENT_OF_SHUSHU_SOUL((byte) 115),
    PET_POTION((byte) 116),
    EQUIPMENT((byte) 118),
    MUSHROOM((byte) 119),
    PETSMOUNT((byte) 121),
    PETSMOUNT_POTION((byte) 122),
    PETSMOUNT_CERTIFICATE((byte) 123),
    PETSMOUNT_GHOST((byte) 124),
    SOUVENIR((byte) 125),
    MAIN_QUESTS((byte) 126),
    TEMPLE_QUESTS((byte) 127),
    WANTED_NOTICE((byte) 128),
    ALIGNMENT((byte) 129),
    CITY_ORDER((byte) 130),
    EVENT((byte) 131),
    ARCHIPELAGO_OF_VULKANIA((byte) 132),
    ASTRUB((byte) 133),
    CITY_QUESTS((byte) 134),
    BWORK_CAMP((byte) 136),
    CANIA((byte) 137),
    AMAKNA_CASTLE((byte) 138),
    FRIGOST_ISLAND((byte) 139),
    OTOMAI_ISLAND((byte) 140),
    KWISMAS_ISLAND((byte) 141),
    INCARNAM((byte) 142),
    KOALAK_MOUNTAIN((byte) 143),
    PANDALA((byte) 144),
    MADRESTAM_HARBOUR((byte) 145),
    PROVINCE_OF_AMAKNA((byte) 146),
    KROSMOZ((byte) 147),
    TOKEN((byte) 148),
    MOON_ISLAND((byte) 149),
    WABBIT_ISLANDS((byte) 150),
    TROPHY((byte) 151),
    PEBBLE((byte) 152),
    KWISMAS((byte) 153),
    WRAPPING_PAPER((byte) 154),
    SUFOKIA((byte) 155),
    ALMANAX((byte) 156),
    FIGURINE((byte) 157),
    CARAMEL_RABMAJOKE((byte) 158),
    STRAWBERRY_RABMAJOKE((byte) 159),
    LEMON_RABMAJOKE((byte) 160),
    ORANGE_RABMAJOKE((byte) 161),
    KOLA_RABMAJOKE((byte) 162),
    NOUGAT_RABMAJOKE((byte) 163),
    GARMENT((byte) 164),
    CONQUEST_POTION((byte) 165),
    MIMISYMBIC((byte) 166),
    DUNGEON_KEEPER_ESSENCE((byte) 167),
    VIGILANTES((byte) 168),
    SIDEKICK((byte) 169),
    DIVINE_DIMENSIONS((byte) 171),
    CHEST((byte) 172),
    EMOTE_SCROLL((byte) 173),
    MAP((byte) 174),
    MAP_FRAGMENT((byte) 175),
    BOX_OF_FRAGMENTS((byte) 176),
    ;
    
    public final byte value;

    WorldItemType(byte value) {
        this.value = value;
    }

    public boolean isEquipment() {
        if (this.isWeapon()) return true;

        switch (this) {
            case AMULET:
            case SHOVEL:
            case RING:
            case BELT:
            case BOOTS:
            case HAT:
            case CLOAK:
            case PET:
            case DOFUS:
                return true;

            default:
                return false;
        }
    }

    public boolean isWeapon() {
        switch (this) {
            case AXE:
            case PICKAXE:
            case SCYTHE:
            case BOW:
            case WAND:
            case STAFF:
            case DAGGER:
            case SWORD:
            case HAMMER:
            case SHOVEL:
          //case TOOL:
                return true;

            default:
                return false;
        }
    }

    public boolean canBeMovedTo(CharacterInventoryPositionEnum position) {
        switch (position) {
            // everyone can be unequiped
            case INVENTORY_POSITION_NOT_EQUIPED: return true;
            // nobody can go on mount position
            case INVENTORY_POSITION_MOUNT: return false;

            case ACCESSORY_POSITION_WEAPON: return this.isWeapon();

            case ACCESSORY_POSITION_HAT:        return this == HAT;
            case ACCESSORY_POSITION_CAPE:       return this == CLOAK;
            case ACCESSORY_POSITION_BELT:       return this == BELT;
            case ACCESSORY_POSITION_BOOTS:      return this == BOOTS;
            case ACCESSORY_POSITION_AMULET:     return this == AMULET;
            case ACCESSORY_POSITION_SHIELD:     return this == SHIELD;
            case ACCESSORY_POSITION_PETS:       return this == PET;
            case INVENTORY_POSITION_RING_LEFT:
            case INVENTORY_POSITION_RING_RIGHT: return this == RING;
            case INVENTORY_POSITION_DOFUS_1:
            case INVENTORY_POSITION_DOFUS_2:
            case INVENTORY_POSITION_DOFUS_3:
            case INVENTORY_POSITION_DOFUS_4:
            case INVENTORY_POSITION_DOFUS_5:
            case INVENTORY_POSITION_DOFUS_6:    return this == DOFUS;


            // TODO(world/items): not sure here where they can be moved
            case INVENTORY_POSITION_MUTATION:
            case INVENTORY_POSITION_BOOST_FOOD:
            case INVENTORY_POSITION_FIRST_BONUS:
            case INVENTORY_POSITION_SECOND_BONUS:
            case INVENTORY_POSITION_FIRST_MALUS:
            case INVENTORY_POSITION_SECOND_MALUS:
            case INVENTORY_POSITION_ROLEPLAY_BUFFER:
            case INVENTORY_POSITION_FOLLOWER:
            case INVENTORY_POSITION_COMPANION:

            default:
                return false;
        }
    }

    public static WorldItemType valueOf(byte value) {
        for (WorldItemType it : values()) {
            if (it.value == value) {
                return it;
            }
        }
        throw new NoSuchElementException();
    }
}
