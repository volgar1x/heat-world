package org.heat.world.metrics;

import com.google.common.primitives.Shorts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.heat.shared.Arithmetics;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public final class LimitStat implements GameStat {
    final GameStats<LimitStat> id;

    short current;
    short min, max;

    @Override
    public short getTotal() {
        return current;
    }

    public void plus(short current) {
        setCurrent(Arithmetics.addShorts(this.current, current));
    }
    public void minus(short current) {
        plus((short) -current);
    }

    public void setCurrent(short current) {
        if (current < min) {
            current = min;
        } else if (current > max) {
            current = max;
        }
        this.current = current;
    }

    public void setMin(short min) {
        if (min >= max) {
            throw new IllegalArgumentException();
        }
        this.min = min;

        if (this.current < this.min) {
            this.current = this.min;
        }
    }

    public void setMax(short max) {
        if (max <= min) {
            throw new IllegalArgumentException();
        }

        this.max = max;

        if (this.current > this.max) {
            this.current = this.max;
        }
    }

    public void setCurrentAndMax(short currentAndMax) {
        setMax(currentAndMax);
        setCurrent(currentAndMax);
    }

    public void setCurrentAndMax(short current, short max) {
        setMax(max);
        setCurrent(current);
    }

    @Override
    public void plusEquipment(short equipment) {
        // i'm sure here
        setMax(Shorts.checkedCast(getMax() + equipment));

        // less here
        plus(equipment);
    }

    @Override
    public LimitStat copy() {
        return new LimitStat(id, current, min, max);
    }
}
