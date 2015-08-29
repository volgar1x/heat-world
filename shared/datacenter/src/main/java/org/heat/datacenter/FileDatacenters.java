package org.heat.datacenter;

import lombok.SneakyThrows;
import org.heat.dofus.d2o.D2oReader;
import org.heat.dofus.d2o.metadata.ModuleDefinition;
import org.heat.shared.Pair;
import org.heat.shared.stream.MoreCollectors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileDatacenters {
    private FileDatacenters() {}

    public static Class<?> getMainDataClass(ModuleDefinition mod) {
        if (mod.getClasses().size() == 1) {
            return mod.getClasses().values().iterator().next().getDataClass();
        }

        return mod.getClasses().values().stream()
                .filter(x -> mod.getName().contains(x.getName()))
                .map(x -> x.getDataClass())
                .collect(MoreCollectors.unique());
    }

    @SneakyThrows
    public static Stream<Path> getModulePathStream(Path path) {
        return Files.list(path).filter(x -> x.getFileName().toString().endsWith(".d2o"));
    }

    public static Stream<ModuleDefinition> getModuleStream(Stream<Path> paths, D2oReader reader) {
        return paths.map(reader::loadModule);
    }

    public static Stream<Pair<ModuleDefinition, Map<Integer, Object>>> getObjectStream(Stream<ModuleDefinition> mods, D2oReader reader) {
        return mods.map(mod -> Pair.of(mod, reader.loadMap(mod)));
    }

    public static Map<Class<?>, Map<Integer, Object>> collectObjectStream(Stream<Pair<ModuleDefinition, Map<Integer, Object>>> stream) {
        return stream.collect(Collectors.toMap(
                x -> getMainDataClass(x.first),
                x -> x.second
        ));
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> bufferize(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(LinkedList::new)).stream();
    }

    public static Map<Class<?>, Map<Integer, Object>> load(Path path, D2oReader reader) {
        Stream<Path> paths = getModulePathStream(path);

        Stream<ModuleDefinition> mods = getModuleStream(paths, reader);

        Stream<Pair<ModuleDefinition, Map<Integer, Object>>> objects = getObjectStream(bufferize(mods).parallel(), reader);

        return collectObjectStream(objects);
    }
}
