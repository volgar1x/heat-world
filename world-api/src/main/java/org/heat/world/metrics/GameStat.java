package org.heat.world.metrics;

public interface GameStat {
    GameStats<?> getId();
    short getTotal();

    void plusEquipment(short equipment);

    GameStat copy();

    default short getSafeTotal() {
        short total = getTotal();
        return total >= 0 ? total : (short) 0;
    }

    default short getAbsTotal() {
        short total = getTotal();
        return total >= 0 ? total : (short) -total;
    }
}
