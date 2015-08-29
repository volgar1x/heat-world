package org.heat.datacenter;

import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.dofus.d2p.DlmReader;
import org.heat.dofus.d2p.PakReader;
import org.heat.dofus.d2p.PakRegistry;
import org.heat.dofus.d2p.maps.DofusMap;
import org.heat.shared.Stopwatch;
import org.rocket.StartReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class PermEarlyMapDatacenter<T extends DofusMap> implements MapDatacenter<T> {

    public static final Logger log = LoggerFactory.getLogger(PermEarlyMapDatacenter.class);

    private final Path pakPath;
    private final Supplier<T> mapSupplier;
    private final byte[] key;

    private Map<Long, T> maps;

    public PermEarlyMapDatacenter(Path pakPath, Supplier<T> mapSupplier, byte[] key) {
        this.pakPath = pakPath;
        this.mapSupplier = mapSupplier;
        this.key = key;
    }

    @Override
    public void start(StartReason reason) {
        PakRegistry pak = PakReader.read(pakPath);

        Stopwatch sw = Stopwatch.system();
        try (Stopwatch.H ignored = sw.start()) {
            maps = pak.getSubRegistries().values().stream()
                    .flatMap(reg -> reg.getIndexes().values().stream())
                    .map(index -> {
                        T map = DlmReader.load(mapSupplier, key, index);
                        map.setLayers(null);
                        map.setBackground(null);
                        map.setForeground(null);
                        return map;
                    })
                    .parallel()
                    .collect(Collectors.toMap(DofusMap::getId, Function.identity()));
        }

        log.debug("{} maps has been loaded in {} ms", maps.size(), sw.elapsed().toMillis());
    }

    @Override
    public void stop() {
        maps.clear();
        maps = null;
    }

    @Override
    public Future<T> fetch(long id) {
        if (!maps.containsKey(id)) {
            return Future.never();
        }
        T map = maps.get(id);
        return Futures.success(map);
    }
}
