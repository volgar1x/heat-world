package org.heat.world.metrics;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class Experiences {
    private Experiences() {}

    public static final int PLAYER_COLUMN = 1;

    @SneakyThrows
    public static Experience read(Path path, int column) {
        double[] experiences = Files.lines(path)
                .map(line -> line.split("-")[column])
                .filter(s -> !s.isEmpty())
                .mapToDouble(Double::parseDouble)
                .sorted()
                .toArray();

        Experience res = null;

        int level = experiences.length;
        for (int i = experiences.length - 1; i >= 0; i--) {
            res = new Experience(level--, experiences[i], Optional.ofNullable(res));
        }

        return res;
    }
}
