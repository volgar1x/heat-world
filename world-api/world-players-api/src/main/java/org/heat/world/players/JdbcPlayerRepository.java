package org.heat.world.players;

import lombok.SneakyThrows;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Worker;
import org.heat.shared.database.JdbcRepositoryNG;
import org.heat.shared.database.Table;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public final class JdbcPlayerRepository extends JdbcRepositoryNG<Player> implements PlayerRepository {
    private final DataSource dataSource;

    @Inject
    public JdbcPlayerRepository(Table<Player> table, DataSource dataSource, Worker worker) {
        super(table, worker);
        this.dataSource = dataSource;
    }

    @SneakyThrows
    @Override
    protected Connection getConnection() {
        return dataSource.getConnection();
    }

    @Override
    protected String createDeleteQuery() {
        return "UPDATE players SET deleted_at=CURRENT_TIMESTAMP WHERE id=:id";
    }

    @Override
    protected String createSelectQuery() {
        return super.createSelectQuery() + " where deleted_at is null";
    }

    @Override
    protected String createSelectWhereQuery(String column) {
        return this.createSelectQuery() + " and " + column + "=:" + column;
    }

    @Override
    public Future<AtomicInteger> createIdGenerator() {
        return getWorker().submit(() -> {
            int id;

            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("select max(id) as id from players"))
            {
                id = resultSet.next()
                    ? resultSet.getInt("id")
                    : 0;
            }

            return new AtomicInteger(id);
        });
    }

    @Override
    public Future<Player> find(int id) {
        return findFirstByIntColumn("id", id);
    }

    @Override
    public Future<List<Player>> findByUserId(int userId) {
        return findListByIntColumn("userId", userId);
    }

    @Override
    public Future<Player> findByName(String name) {
        return findFirstByColumn("name", name);
    }

    @Override
    public Future<Unit> create(Player player) {
        return insert(player).toUnit();
    }

    @Override
    public Future<Unit> save(Player player) {
        return update(player).toUnit();
    }

    @Override
    public Future<Unit> remove(Player player) {
        return delete(player).toUnit();
    }
}
