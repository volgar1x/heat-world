package org.heat.world.players.items;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.heat.shared.database.Table;
import org.heat.world.items.WorldItem;
import org.heat.world.items.WorldItemRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class JdbcPlayerItemRepository implements PlayerItemRepository {
    private final DataSource dataSource;
    private final Table<WorldItem> itemTable;
    private final WorldItemRepository itemRepository;
    private final Worker worker;

    @Inject
    public JdbcPlayerItemRepository(DataSource dataSource, @Named("backdoor") Table<WorldItem> itemTable, WorldItemRepository itemRepository, Worker worker) {
        this.dataSource = dataSource;
        this.itemTable = itemTable;
        this.itemRepository = itemRepository;
        this.worker = worker;
    }

    public static final String FIND_ITEMS_BY_PLAYER_SQL =
            "select i.* " +
            "from player_items pi " +
            "inner join items i on i.uid=pi.item_uid " +
            "where pi.player_id=?"
            ;

    private String batchInsertQuery(int rows) {
        StringBuilder builder = new StringBuilder("insert into player_items(player_id, item_uid) values ");
        builder.append("(?,?)");
        for (int i = 1; i < rows; i++) {
            builder.append(",(?,?)");
        }
        return builder.toString();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Future<List<WorldItem>> findItemsByPlayer(int playerId) {
        return worker.submit(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(FIND_ITEMS_BY_PLAYER_SQL))
            {
                statement.setInt(1, playerId);

                List<WorldItem> items = new ArrayList<>();
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // NOTE(Blackrush): dirty but fine as long as WorldItemTable doesn't perform async operations
                        items.add(itemTable.importFromDb(resultSet).get());
                    }
                }

                this.itemRepository.transferOwnership(items);

                return items;
            }
        });
    }

    @Override
    public Future<Unit> persist(int playerId, int itemId) {
        return worker.cast(() -> {
            try (Connection co = dataSource.getConnection()) {
                try (PreparedStatement s = co.prepareStatement("insert into player_items(player_id, item_uid) values(?, ?)")) {
                    s.setInt(1, playerId);
                    s.setInt(2, itemId);

                    s.executeUpdate();
                }
            }
        });
    }

    @Override
    public Future<Unit> persistAll(int playerId, IntStream itemIds) {
        int[] uids = itemIds.toArray();

        if (uids.length == 0) {
            return Futures.unit();
        }

        return worker.cast(() -> {
            try (Connection co = dataSource.getConnection()) {
                try (PreparedStatement s = co.prepareStatement(batchInsertQuery(uids.length))) {
                    for (int i = 0, j = 1; i < uids.length; i++, j += 2) {
                        s.setInt(j, playerId);
                        s.setInt(j + 1, uids[i]);
                    }

                    s.execute();
                }
            }
        });
    }

    @Override
    public Future<Unit> remove(int playerId, int itemId) {
        return worker.cast(() -> {
            try (Connection co = dataSource.getConnection()) {
                try (PreparedStatement s = co.prepareStatement("delete from player_items where player_id=? and item_uid=?")) {
                    s.setInt(1, playerId);
                    s.setInt(2, itemId);

                    s.executeUpdate();
                }
            }
        });
    }
}
