package org.heat.world.players.shortcuts;

import lombok.SneakyThrows;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Worker;
import org.heat.shared.database.JdbcRepositoryNG;
import org.heat.shared.database.Table;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class JdbcPlayerShortcutRepository extends JdbcRepositoryNG<PlayerShortcut> implements PlayerShortcutRepository {
    private final DataSource dataSource;

    @Inject
    public JdbcPlayerShortcutRepository(Table<PlayerShortcut> table, DataSource dataSource, Worker worker) {
        super(table, worker);
        this.dataSource = dataSource;
    }

    @Override
    protected String createSelectQuery() {
        return "select i.gid as item_gid, ps.* " +
                "from player_shortcuts ps " +
                "left join items i on i.uid=ps.item_uid";
    }

    @Override
    protected String createDeleteQuery() {
        return
            "delete from " + getTable().getTableName() + " " +
            "where " +
            getTable().getPrimaryKeys().stream()
                    .map(column -> column + "=:" + column)
                    .collect(Collectors.joining(" AND "))
            ;
    }

    @SneakyThrows
    @Override
    protected Connection getConnection() {
        return dataSource.getConnection();
    }

    @Override
    public Future<List<PlayerShortcut>> findAll(int playerId) {
        return findListByIntColumn("player_id", playerId);
    }

    @Override
    public Future<PlayerShortcut> create(PlayerShortcut shortcut) {
        return insert(shortcut);
    }

    @Override
    public Future<PlayerShortcut> remove(PlayerShortcut shortcut) {
        return delete(shortcut);
    }
}
