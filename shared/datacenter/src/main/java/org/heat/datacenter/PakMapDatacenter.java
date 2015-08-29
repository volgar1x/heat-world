package org.heat.datacenter;

import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Worker;
import org.heat.dofus.d2p.DlmReader;
import org.heat.dofus.d2p.PakReader;
import org.heat.dofus.d2p.PakRegistry;
import org.heat.dofus.d2p.maps.DofusMap;
import org.heat.shared.Stopwatch;
import org.rocket.StartReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.function.Supplier;

public final class PakMapDatacenter<T extends DofusMap> implements MapDatacenter<T> {
    public static final Logger log = LoggerFactory.getLogger(PakMapDatacenter.class);

    private final Path pakPath;
    private final Supplier<T> mapSupplier;
    private final byte[] key;
    private final Worker worker;

    private PakRegistry pak;
    private long loadAvg = -1;

    public PakMapDatacenter(Path pakPath, Supplier<T> mapSupplier, byte[] key, Worker worker) {
        this.pakPath = pakPath;
        this.mapSupplier = mapSupplier;
        this.key = key;
        this.worker = worker;
    }

    @Override
    public void start(StartReason reason) {
        pak = PakReader.read(pakPath);
    }

    @Override
    public void stop() {
        pak = null;
    }

    @Override
    public Future<T> fetch(long id) {
        return worker.submit(() -> load(id));
    }

    private T load(long id) {
        Stopwatch sw = Stopwatch.system();
        try (Stopwatch.H ignored = sw.start()) {
            return DlmReader.load(mapSupplier, key, pak, (int) id);
        } finally {
            long nanos = sw.elapsed().toNanos();
            if (loadAvg == -1) {
                loadAvg = nanos;
            } else {
                loadAvg = (loadAvg + nanos) / 2;
            }
            log.debug("map {} loaded in {} ns (avg {} ns)", id, nanos, loadAvg);
        }
    }
}
