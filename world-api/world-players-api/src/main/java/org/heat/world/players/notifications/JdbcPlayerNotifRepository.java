package org.heat.world.players.notifications;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.heat.shared.database.Table;
import org.heat.world.items.WorldItem;
import org.heat.world.items.WorldItemRepository;
import org.heat.world.players.items.PlayerItemRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class JdbcPlayerNotifRepository implements PlayerNotifRepository {
    private final DataSource dataSource;
    private final Worker worker;

    @Inject
    public JdbcPlayerNotifRepository(DataSource dataSource, Worker worker) {
        this.dataSource = dataSource;
        this.worker = worker;
    }

    public static final String FIND_NOTIFS_BY_PLAYER_SQL =
            "select notif_id " +
            "from player_notifs pi " +
            "inner join players i on i.id=pi.player_id " +
            "where pi.player_id=?"
            ;

    @SuppressWarnings("deprecation")
    @Override
    public Future<int[]> findAll(int playerId) {
        return worker.submit(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(FIND_NOTIFS_BY_PLAYER_SQL))
            {
                statement.setInt(1, playerId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.last()) {
                        return new int[0];
                    }

                    int[] ids = new int[resultSet.getRow()+1];
                    resultSet.beforeFirst();
                    for (int i = 0; i < ids.length && resultSet.next(); i++) {
                        ids[i] = resultSet.getInt(1);
                    }
                    return ids;
                }
            }
        });
    }

    @Override
    public Future<Unit> save(int playerId, int notifId) {
        return worker.cast(() -> {
            try (Connection co = dataSource.getConnection()) {
                try (PreparedStatement s = co.prepareStatement("insert into player_notifs(player_id, notif_id) values(?, ?)")) {
                    s.setInt(1, playerId);
                    s.setInt(2, notifId);

                    s.executeUpdate();
                }
            }
        });
    }

    @Override
    public Future<Unit> removeAll(int playerId) {
        return worker.cast(() -> {
            try (Connection co = dataSource.getConnection()) {
                try (PreparedStatement s = co.prepareStatement("delete from player_notifs where player_id=?")) {
                    s.setInt(1, playerId);
                    s.executeUpdate();
                }
            }
        });
    }
}
