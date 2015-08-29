package org.heat.world.players.metrics;

import lombok.Value;
import lombok.experimental.Wither;
import org.heat.world.metrics.Experience;

@Value
@Wither
public final class PlayerExperience {
    final double current;
    final Experience step;

    /**
     * Return the current level
     * @return a positive integer
     */
    public int getCurrentLevel() {
        return step.getLevel();
    }

    /**
     * Return a boolean indicating whether or not this is the maximum level a player can get
     * @return a boolean
     */
    public boolean hasReachedTopLevel() {
        return !step.getNext().isPresent();
    }

    /**
     * Return the needed amount of experience to the next level.
     * @return a positive amount of experience
     */
    public double getNeededExperience() {
        return step.getTop() - current;
    }

    /**
     * Add a strictly positive amount of levels
     * @param levels a strictly positive integer
     * @return the resulting {@link PlayerExperience}
     */
    public PlayerExperience plusLevels(int levels) {
        if (levels < 0) {
            throw new IllegalArgumentException("you can not remove levels");
        }
        if (levels == 0) {
            return this;
        }

        Experience it = step;
        Experience last = step;
        for (int i = 0; i < levels; i++) {
            if (!it.getNext().isPresent()) {
                break;
            }
            last = it;
            it = it.getNext().get();
        }

        return new PlayerExperience(last.getTop(), it);
    }

    /**
     * Add a strictly positive amount of experience. This may add one or more levels.
     * @param experience a strictly positive floating number
     * @return the resulting {@link PlayerExperience}
     */
    public PlayerExperience plusExperience(double experience) {
        if (experience < 0.0) {
            throw new IllegalArgumentException("you can not remove experience");
        }
        if (experience == 0.0) {
            return this;
        }

        double newExperience = current + experience;
        Experience newStep = step;
        while (newStep.getTop() < newExperience) {
            if (!newStep.getNext().isPresent()) {
                break;
            }
            newStep = newStep.getNext().get();
        }

        return new PlayerExperience(newExperience, newStep);
    }
}
