package org.heat.world.metrics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class GameStats<T extends GameStat> implements Serializable {
    private transient static int NEXT_ID = 1;

    public final int id;
    public final String name;
    public final boolean boostable;

    private final Function<GameStats<T>, T> instantiator;
    
    private GameStats(String name, Function<GameStats<T>, T> instantiator, boolean boostable) {
        this.id = NEXT_ID++;
        this.name = name;
        this.instantiator = instantiator;
        this.boostable = boostable;
    }

    private GameStats(String name, Function<GameStats<T>, T> instantiator) {
        this(name, instantiator, false);
    }

    public T createStat() {
        return instantiator.apply(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStats<?> that = (GameStats<?>) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static Optional<GameStats<?>> ofId(int id) {
        return values.stream().filter(x -> x.id == id).findAny();
    }

    @SuppressWarnings("unchecked")
    public static Optional<GameStats<RegularStat>> findBoostable(byte id) {
        // NOTE(Blackrush): hack-ish, but get shit done
        return (Optional) ofId(id - 3).filter(x -> x.boostable);
    }

    public static ImmutableMap<GameStats<?>, GameStat> newFilledMap() {
        ImmutableMap.Builder<GameStats<?>, GameStat> map = ImmutableMap.builder();
        for (GameStats<?> value : values) {
            map.put(value, value.createStat());
        }
        return map.build();
    }

    public static ImmutableMap<GameStats<?>, GameStat> copy(ImmutableMap<GameStats<?>, GameStat> map) {
        ImmutableMap.Builder<GameStats<?>, GameStat> copy = ImmutableMap.builder();
        for (Map.Entry<GameStats<?>, GameStat> entry : map.entrySet()) {
            GameStat copiedStat = entry.getValue().copy();
            copy.put(entry.getKey(), copiedStat);
        }
        return copy.build();
    }

    public static final GameStats<LimitStat> LIFE = new GameStats<>("LIFE", LimitStat::new );
    public static final GameStats<LimitStat> ENERGY = new GameStats<>("ENERGY", LimitStat::new );
    public static final GameStats<RegularStat> INITIATIVE = new GameStats<>("INITIATIVE", RegularStat::new);
    public static final GameStats<RegularStat> PROSPECTING = new GameStats<>("PROSPECTING", RegularStat::new );
    public static final GameStats<RegularStat> ACTIONS = new GameStats<>("ACTIONS", RegularStat::new );
    public static final GameStats<RegularStat> MOVEMENTS = new GameStats<>("MOVEMENTS", RegularStat::new );
    public static final GameStats<RegularStat> STRENGTH = new GameStats<>("STRENGTH", RegularStat::new, true);
    public static final GameStats<RegularStat> VITALITY = new GameStats<>("VITALITY", RegularStat::new, true);
    public static final GameStats<RegularStat> WISDOM = new GameStats<>("WISDOM", RegularStat::new, true);
    public static final GameStats<RegularStat> CHANCE = new GameStats<>("CHANCE", RegularStat::new, true);
    public static final GameStats<RegularStat> AGILITY = new GameStats<>("AGILITY", RegularStat::new, true);
    public static final GameStats<RegularStat> INTELLIGENCE = new GameStats<>("INTELLIGENCE", RegularStat::new, true);
    public static final GameStats<RegularStat> RANGE = new GameStats<>("RANGE", RegularStat::new );
    public static final GameStats<RegularStat> SUMMONABLE_CREATURES = new GameStats<>("SUMMONABLE_CREATURES", RegularStat::new );
    public static final GameStats<RegularStat> REFLECT = new GameStats<>("REFLECT", RegularStat::new );
    public static final GameStats<RegularStat> CRITICAL_HIT = new GameStats<>("CRITICAL_HIT", RegularStat::new );
    public static final GameStats<SingleStat> CRITICAL_HIT_WEAPON = new GameStats<>("CRITICAL_HIT_WEAPON", SingleStat::new );
    public static final GameStats<RegularStat> CRITICAL_MISS = new GameStats<>("CRITICAL_MISS", RegularStat::new );
    public static final GameStats<RegularStat> HEAL = new GameStats<>("HEAL", RegularStat::new );
    public static final GameStats<RegularStat> ALL_DAMAGES = new GameStats<>("ALL_DAMAGES", RegularStat::new );
    public static final GameStats<RegularStat> WEAPON_DAMAGES_PERCENT = new GameStats<>("WEAPON_DAMAGES_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> DAMAGES_PERCENT = new GameStats<>("DAMAGES_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> TRAP = new GameStats<>("TRAP", RegularStat::new );
    public static final GameStats<RegularStat> TRAP_PERCENT = new GameStats<>("TRAP_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> GLYPH_PERCENT = new GameStats<>("GLYPH_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> PERMANENT_DAMAGE_PERCENT = new GameStats<>("PERMANENT_DAMAGE_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> TACKLE_BLOCK = new GameStats<>("TACKLE_BLOCK", RegularStat::new );
    public static final GameStats<RegularStat> TACKLE_EVADE = new GameStats<>("TACKLE_EVADE", RegularStat::new );
    public static final GameStats<RegularStat> PA_ATTACK = new GameStats<>("PA_ATTACK", RegularStat::new );
    public static final GameStats<RegularStat> PM_ATTACK = new GameStats<>("PM_ATTACK", RegularStat::new );
    public static final GameStats<RegularStat> PUSH_DAMAGE = new GameStats<>("PUSH_DAMAGE", RegularStat::new );
    public static final GameStats<RegularStat> CRITICAL_DAMAGE = new GameStats<>("CRITICAL_DAMAGE", RegularStat::new );
    public static final GameStats<RegularStat> NEUTRAL_DAMAGE = new GameStats<>("NEUTRAL_DAMAGE", RegularStat::new );
    public static final GameStats<RegularStat> EARTH_DAMAGE = new GameStats<>("EARTH_DAMAGE", RegularStat::new );
    public static final GameStats<RegularStat> WATER_DAMAGE = new GameStats<>("WATER_DAMAGE", RegularStat::new );
    public static final GameStats<RegularStat> AIR_DAMAGE = new GameStats<>("AIR_DAMAGE", RegularStat::new );
    public static final GameStats<RegularStat> FIRE_DAMAGE = new GameStats<>("FIRE_DAMAGE", RegularStat::new );
    public static final GameStats<RegularStat> DODGE_PA_LOST_PROBABILITY = new GameStats<>("DODGE_PA_LOST_PROBABILITY", RegularStat::new );
    public static final GameStats<RegularStat> DODGE_PM_LOST_PROBABILITY = new GameStats<>("DODGE_PM_LOST_PROBABILITY", RegularStat::new );
    public static final GameStats<RegularStat> NEUTRAL_ELEMENT_RESIST_PERCENT = new GameStats<>("NEUTRAL_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> EARTH_ELEMENT_RESIST_PERCENT = new GameStats<>("EARTH_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> WATER_ELEMENT_RESIST_PERCENT = new GameStats<>("WATER_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> AIR_ELEMENT_RESIST_PERCENT = new GameStats<>("AIR_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> FIRE_ELEMENT_RESIST_PERCENT = new GameStats<>("FIRE_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> NEUTRAL_ELEMENT_REDUCTION = new GameStats<>("NEUTRAL_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> EARTH_ELEMENT_REDUCTION = new GameStats<>("EARTH_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> WATER_ELEMENT_REDUCTION = new GameStats<>("WATER_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> AIR_ELEMENT_REDUCTION = new GameStats<>("AIR_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> FIRE_ELEMENT_REDUCTION = new GameStats<>("FIRE_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> PUSH_DAMAGE_REDUCTION = new GameStats<>("PUSH_DAMAGE_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> CRITICAL_DAMAGE_REDUCTION = new GameStats<>("CRITICAL_DAMAGE_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> PVP_NEUTRAL_ELEMENT_RESIST_PERCENT = new GameStats<>("PVP_NEUTRAL_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> PVP_EARTH_ELEMENT_RESIST_PERCENT = new GameStats<>("PVP_EARTH_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> PVP_WATER_ELEMENT_RESIST_PERCENT = new GameStats<>("PVP_WATER_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> PVP_AIR_ELEMENT_RESIST_PERCENT = new GameStats<>("PVP_AIR_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> PVP_FIRE_ELEMENT_RESIST_PERCENT = new GameStats<>("PVP_FIRE_ELEMENT_RESIST_PERCENT", RegularStat::new );
    public static final GameStats<RegularStat> PVP_NEUTRAL_ELEMENT_REDUCTION = new GameStats<>("PVP_NEUTRAL_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> PVP_EARTH_ELEMENT_REDUCTION = new GameStats<>("PVP_EARTH_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> PVP_WATER_ELEMENT_REDUCTION = new GameStats<>("PVP_WATER_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> PVP_AIR_ELEMENT_REDUCTION = new GameStats<>("PVP_AIR_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<RegularStat> PVP_FIRE_ELEMENT_REDUCTION = new GameStats<>("PVP_FIRE_ELEMENT_REDUCTION", RegularStat::new );
    public static final GameStats<SingleStat> PODS = new GameStats<>("PODS", SingleStat::new);

    public static final ImmutableList<GameStats<?>> values =
            ImmutableList.of(
                    LIFE,
                    ENERGY,
                    INITIATIVE,
                    PROSPECTING,
                    ACTIONS,
                    MOVEMENTS,
                    STRENGTH,
                    VITALITY,
                    WISDOM,
                    CHANCE,
                    AGILITY,
                    INTELLIGENCE,
                    RANGE,
                    SUMMONABLE_CREATURES,
                    REFLECT,
                    CRITICAL_HIT,
                    CRITICAL_HIT_WEAPON,
                    CRITICAL_MISS,
                    HEAL,
                    ALL_DAMAGES,
                    WEAPON_DAMAGES_PERCENT,
                    DAMAGES_PERCENT,
                    TRAP,
                    TRAP_PERCENT,
                    GLYPH_PERCENT,
                    PERMANENT_DAMAGE_PERCENT,
                    TACKLE_BLOCK,
                    TACKLE_EVADE,
                    PA_ATTACK,
                    PM_ATTACK,
                    PUSH_DAMAGE,
                    CRITICAL_DAMAGE,
                    NEUTRAL_DAMAGE,
                    EARTH_DAMAGE,
                    WATER_DAMAGE,
                    AIR_DAMAGE,
                    FIRE_DAMAGE,
                    DODGE_PA_LOST_PROBABILITY,
                    DODGE_PM_LOST_PROBABILITY,
                    NEUTRAL_ELEMENT_RESIST_PERCENT,
                    EARTH_ELEMENT_RESIST_PERCENT,
                    WATER_ELEMENT_RESIST_PERCENT,
                    AIR_ELEMENT_RESIST_PERCENT,
                    FIRE_ELEMENT_RESIST_PERCENT,
                    NEUTRAL_ELEMENT_REDUCTION,
                    EARTH_ELEMENT_REDUCTION,
                    WATER_ELEMENT_REDUCTION,
                    AIR_ELEMENT_REDUCTION,
                    FIRE_ELEMENT_REDUCTION,
                    PUSH_DAMAGE_REDUCTION,
                    CRITICAL_DAMAGE_REDUCTION,
                    PVP_NEUTRAL_ELEMENT_RESIST_PERCENT,
                    PVP_EARTH_ELEMENT_RESIST_PERCENT,
                    PVP_WATER_ELEMENT_RESIST_PERCENT,
                    PVP_AIR_ELEMENT_RESIST_PERCENT,
                    PVP_FIRE_ELEMENT_RESIST_PERCENT,
                    PVP_NEUTRAL_ELEMENT_REDUCTION,
                    PVP_EARTH_ELEMENT_REDUCTION,
                    PVP_WATER_ELEMENT_REDUCTION,
                    PVP_AIR_ELEMENT_REDUCTION,
                    PVP_FIRE_ELEMENT_REDUCTION,
                    PODS
            );
}
