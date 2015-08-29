package org.heat.world.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.heat.shared.Arithmetics;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public final class SingleStat implements GameStat {
    final GameStats<SingleStat> id;

    short current;

    @Override
    public short getTotal() {
        return current;
    }

    public void plus(short current) {
        this.current = Arithmetics.addShorts(this.current, current);
    }

    @Override
    public void plusEquipment(short equipment) {
        plus(equipment);
    }

    @Override
    public SingleStat copy() {
        return new SingleStat(id, current);
    }
}
