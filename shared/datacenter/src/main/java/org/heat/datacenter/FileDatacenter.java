package org.heat.datacenter;

import org.heat.dofus.d2o.D2oReader;
import org.heat.shared.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

public final class FileDatacenter extends PermEarlyDatacenter {
    public static final Logger log = LoggerFactory.getLogger(FileDatacenter.class);

    private final Path path;
    private final D2oReader reader;

    public FileDatacenter(Path path, D2oReader reader) {
        this.path = path;
        this.reader = reader;
    }

    @Override
    protected Map<Class<?>, Map<Integer, Object>> load() {
        Stopwatch sw = Stopwatch.system();

        Map<Class<?>, Map<Integer, Object>> objects;
        try (Stopwatch.H ignored = sw.start()) {
            objects = FileDatacenters.load(path, reader);
        }

        int count = objects.values().stream().mapToInt(x -> x.size()).reduce(0, Integer::sum);
        log.debug("{} objects has been loaded in {} ms", count, sw.elapsed().toMillis());

        return objects;
    }
}
