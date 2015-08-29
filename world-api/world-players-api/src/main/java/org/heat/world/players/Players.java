package org.heat.world.players;

import com.ankamagames.dofus.datacenter.breeds.Breed;
import com.ankamagames.dofus.datacenter.spells.Spell;
import com.ankamagames.dofus.datacenter.spells.SpellLevel;
import com.ankamagames.dofus.network.enums.PlayerStatusEnum;
import com.ankamagames.dofus.network.types.game.character.characteristic.CharacterCharacteristicsInformations;
import com.ankamagames.dofus.network.types.game.character.status.PlayerStatus;
import org.heat.datacenter.Datacenter;
import org.heat.shared.IntPair;
import org.heat.world.metrics.GameStatBook;
import org.heat.world.metrics.GameStats;
import org.heat.world.players.metrics.PlayerSpell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.heat.world.metrics.GameStats.*;

public final class Players {
    private Players() {}

    public static final byte
            MIN_SPELL_LEVEL = 1,
            MAX_SPELL_LEVEL = 6;

    public static final int[] MIN_PLAYER_SPELL_LEVELS = new int[] {
            1, 1, 1,
            3, 6, 9,
            13, 17, 21,
            26, 31, 36,
            42, 48, 54,
            60, 70, 80, 90,
            100, 200
    };

    public static final PlayerStatus OFFLINE_STATUS = new PlayerStatus(
            PlayerStatusEnum.PLAYER_STATUS_OFFLINE.value);
    public static final PlayerStatus ONLINE_STATUS  = new PlayerStatus(
            PlayerStatusEnum.PLAYER_STATUS_AVAILABLE.value);

    public static final int NEW_SPELL_DEFAULT_MIN_LEVEL = 1;

    public static final int BASE_TRANSPORTABLE_WEIGHT = 1000;

    public static Optional<long[][]> getStatsPoints(Breed breed, GameStats<?> id) {
        if (id == STRENGTH) return Optional.of(breed.getStatsPointsForStrength());
        if (id == INTELLIGENCE) return Optional.of(breed.getStatsPointsForIntelligence());
        if (id == CHANCE) return Optional.of(breed.getStatsPointsForChance());
        if (id == AGILITY) return Optional.of(breed.getStatsPointsForAgility());
        if (id == VITALITY) return Optional.of(breed.getStatsPointsForVitality());
        if (id == WISDOM) return Optional.of(breed.getStatsPointsForWisdom());
        return Optional.empty();
    }

    public static int getCostOneUpgrade(final long[][] statsPoints, final int actual) {
        for (int i = 0; i < statsPoints.length - 1; i++) {
            if (actual < statsPoints[i + 1][0]) {
                return (int) statsPoints[i][1];
            }
        }
        return (int) statsPoints[statsPoints.length - 1][1];
    }

    public static IntPair upgrade(final long[][] statsPoints, final int actual, final int points) {
        int total = actual;
        int remaining = points;
        while (remaining > 0) {
            int cost = getCostOneUpgrade(statsPoints, total);
            if (remaining - cost < 0) {
                break;
            }

            total++;
            remaining -= cost;
        }
        return IntPair.of(total - actual, points - remaining);
    }

    public static byte checkValidSpellLevel(byte level) {
        if (level < MIN_SPELL_LEVEL || level > MAX_SPELL_LEVEL) {
            throw new IllegalArgumentException("invalid spell level");
        }
        return level;
    }

    public static int getCostUpgradeSpell(byte from, byte to) {
        checkValidSpellLevel(from);
        checkValidSpellLevel(to);

        int cost = 0;
        for (int i = from; i < to; i++) {
            cost += i;
        }
        return cost;
    }

    public static int getSpellMinPlayerLevel(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be positive or zero");
        }
        if (index < MIN_PLAYER_SPELL_LEVELS.length) {
            return MIN_PLAYER_SPELL_LEVELS[index];
        }
        return NEW_SPELL_DEFAULT_MIN_LEVEL;
    }

    public static List<PlayerSpell> buildDefaultBreedSpells(Datacenter datacenter, Breed breed, int actualLevel) {
        List<PlayerSpell> spells = new ArrayList<>();

        for (long spellId : breed.getBreedSpellsId()) {
            Spell spellData = datacenter.find(Spell.class, (int) spellId).get();

            long levelId = spellData.getSpellLevels()[0];
            SpellLevel levelData = datacenter.find(SpellLevel.class, (int) levelId).get();

            if (levelData.getMinPlayerLevel() > actualLevel) {
                continue;
            }

            PlayerSpell spell = new PlayerSpell(datacenter, spellData, levelData, (byte) 1, PlayerSpell.NO_POSITION);
            spells.add(spell);
        }

        return spells;
    }

    public static void populateCharacterCharacteristicsInformations(GameStatBook book, CharacterCharacteristicsInformations infos) {
        infos.lifePoints = book.get(LIFE).getCurrent();
        infos.maxLifePoints = book.get(LIFE).getMax();
        infos.energyPoints = book.get(ENERGY).getCurrent();
        infos.maxEnergyPoints = book.get(ENERGY).getMax();
        infos.initiative = book.get(INITIATIVE).toCharacterBaseCharacteristic();
        infos.prospecting = book.get(PROSPECTING).toCharacterBaseCharacteristic();
        infos.actionPoints = book.get(ACTIONS).toCharacterBaseCharacteristic();
        infos.movementPoints = book.get(MOVEMENTS).toCharacterBaseCharacteristic();
        infos.strength = book.get(STRENGTH).toCharacterBaseCharacteristic();
        infos.vitality = book.get(VITALITY).toCharacterBaseCharacteristic();
        infos.wisdom = book.get(WISDOM).toCharacterBaseCharacteristic();
        infos.chance = book.get(CHANCE).toCharacterBaseCharacteristic();
        infos.agility = book.get(AGILITY).toCharacterBaseCharacteristic();
        infos.intelligence = book.get(INTELLIGENCE).toCharacterBaseCharacteristic();
        infos.range = book.get(RANGE).toCharacterBaseCharacteristic();
        infos.summonableCreaturesBoost = book.get(SUMMONABLE_CREATURES).toCharacterBaseCharacteristic();
        infos.reflect = book.get(REFLECT).toCharacterBaseCharacteristic();
        infos.criticalHit = book.get(CRITICAL_HIT).toCharacterBaseCharacteristic();
        infos.criticalHitWeapon = book.get(CRITICAL_HIT_WEAPON).getCurrent();
        infos.criticalMiss = book.get(CRITICAL_MISS).toCharacterBaseCharacteristic();
        infos.healBonus = book.get(HEAL).toCharacterBaseCharacteristic();
        infos.allDamagesBonus = book.get(ALL_DAMAGES).toCharacterBaseCharacteristic();
        infos.weaponDamagesBonusPercent = book.get(WEAPON_DAMAGES_PERCENT).toCharacterBaseCharacteristic();
        infos.damagesBonusPercent = book.get(DAMAGES_PERCENT).toCharacterBaseCharacteristic();
        infos.trapBonus = book.get(TRAP).toCharacterBaseCharacteristic();
        infos.trapBonusPercent = book.get(TRAP_PERCENT).toCharacterBaseCharacteristic();
        infos.glyphBonusPercent = book.get(GLYPH_PERCENT).toCharacterBaseCharacteristic();
        infos.permanentDamagePercent = book.get(PERMANENT_DAMAGE_PERCENT).toCharacterBaseCharacteristic();
        infos.tackleBlock = book.get(TACKLE_BLOCK).toCharacterBaseCharacteristic();
        infos.tackleEvade = book.get(TACKLE_EVADE).toCharacterBaseCharacteristic();
        infos.PAAttack = book.get(PA_ATTACK).toCharacterBaseCharacteristic();
        infos.PMAttack = book.get(PM_ATTACK).toCharacterBaseCharacteristic();
        infos.pushDamageBonus = book.get(PUSH_DAMAGE).toCharacterBaseCharacteristic();
        infos.criticalDamageBonus = book.get(CRITICAL_DAMAGE).toCharacterBaseCharacteristic();
        infos.neutralDamageBonus = book.get(NEUTRAL_DAMAGE).toCharacterBaseCharacteristic();
        infos.earthDamageBonus = book.get(EARTH_DAMAGE).toCharacterBaseCharacteristic();
        infos.waterDamageBonus = book.get(WATER_DAMAGE).toCharacterBaseCharacteristic();
        infos.airDamageBonus = book.get(AIR_DAMAGE).toCharacterBaseCharacteristic();
        infos.fireDamageBonus = book.get(FIRE_DAMAGE).toCharacterBaseCharacteristic();
        infos.dodgePALostProbability = book.get(DODGE_PA_LOST_PROBABILITY).toCharacterBaseCharacteristic();
        infos.dodgePMLostProbability = book.get(DODGE_PM_LOST_PROBABILITY).toCharacterBaseCharacteristic();
        infos.neutralElementResistPercent = book.get(NEUTRAL_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.earthElementResistPercent = book.get(EARTH_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.waterElementResistPercent = book.get(WATER_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.airElementResistPercent = book.get(AIR_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.fireElementResistPercent = book.get(FIRE_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.neutralElementReduction = book.get(NEUTRAL_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
        infos.earthElementReduction = book.get(EARTH_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
        infos.waterElementReduction = book.get(WATER_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
        infos.airElementReduction = book.get(AIR_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
        infos.fireElementReduction = book.get(FIRE_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
        infos.pushDamageReduction = book.get(PUSH_DAMAGE_REDUCTION).toCharacterBaseCharacteristic();
        infos.criticalDamageReduction = book.get(CRITICAL_DAMAGE_REDUCTION).toCharacterBaseCharacteristic();
        infos.pvpNeutralElementResistPercent = book.get(PVP_NEUTRAL_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.pvpEarthElementResistPercent = book.get(PVP_EARTH_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.pvpWaterElementResistPercent = book.get(PVP_WATER_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.pvpAirElementResistPercent = book.get(PVP_AIR_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.pvpFireElementResistPercent = book.get(PVP_FIRE_ELEMENT_RESIST_PERCENT).toCharacterBaseCharacteristic();
        infos.pvpNeutralElementReduction = book.get(PVP_NEUTRAL_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
        infos.pvpEarthElementReduction = book.get(PVP_EARTH_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
        infos.pvpWaterElementReduction = book.get(PVP_WATER_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
        infos.pvpAirElementReduction = book.get(PVP_AIR_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
        infos.pvpFireElementReduction = book.get(PVP_FIRE_ELEMENT_REDUCTION).toCharacterBaseCharacteristic();
    }
}
