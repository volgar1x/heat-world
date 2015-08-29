package org.heat.world.items;

import org.fungsi.concurrent.Future;

import java.util.List;
import java.util.stream.IntStream;

public interface WorldItemRepository {
    Future<WorldItem> find(int uid);
    Future<List<WorldItem>> find(IntStream uids);

    Future<WorldItem> save(WorldItem item);
    Future<WorldItem> remove(WorldItem item);

    /**
     * Transfer ownership of given items to this repository. Useful when externally loading items
     * @param items a non-null list of items
     */
    @Deprecated
    void transferOwnership(List<WorldItem> items);
}
