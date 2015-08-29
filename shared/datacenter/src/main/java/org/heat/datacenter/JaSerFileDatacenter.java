package org.heat.datacenter;

import org.fungsi.Throwables;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JaSerFileDatacenter extends PermEarlyDatacenter {
    private final Path objectsPath;

    public JaSerFileDatacenter(Path objectsPath) {
        this.objectsPath = objectsPath;
    }

    @Override
    protected Map<Class<?>, Map<Integer, Object>> load() {
        try (InputStream stream = Files.newInputStream(objectsPath)) {
            ObjectInputStream in = new ObjectInputStream(stream);
            return buildIndex(in.readObject());
        } catch (IOException|ClassNotFoundException e) {
            throw Throwables.propagate(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Class<?>, Map<Integer, Object>> buildIndex(Object o) {
        Map<Class<?>, Map<Integer, Object>> result = new HashMap<>();

        List l = (List) o;
        for (Object om : l) {
            Map m = (Map) om;
            for (Object oo : m.values()) {
                result.put(oo.getClass(), m);
            }
        }

        return result;
    }
}
