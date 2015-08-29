package org.heat.world.metrics;

import lombok.ToString;
import lombok.Value;

import java.util.Optional;

@Value
@ToString(exclude = {"next"})
public final class Experience {
    int level;
    double top;
    Optional<Experience> next;

    /**
     * Return a {@link Experience} which `top` field is strictly greater than argument
     * @param experience a double precision float number
     * @return a non-null {@link Experience}
     */
    public Experience getNextUntilEnoughExperience(double experience) {
        if (!next.isPresent() || top >= experience) {
            return this;
        }
        return next.get().getNextUntilEnoughExperience(experience);
    }

    /**
     * Return {@link Experience} which `level` field is equal to argument
     * @param level an integer
     * @return a non-null {@link Experience}
     * @throws java.lang.IllegalStateException when this experience is unable to lookup the right experience
     */
    public Experience getNextUntilIsLevel(int level) {
        if (this.level > level || !next.isPresent()) {
            throw new IllegalStateException();
        }
        if (this.level == level) {
            return this;
        }
        return next.get().getNextUntilIsLevel(level);
    }
}
