package org.heat.login.users;

import lombok.SneakyThrows;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Worker;
import org.heat.User;
import org.heat.shared.database.JdbcRepositoryNG;
import org.heat.shared.database.Table;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;

@Singleton
public final class JdbcUserRepository extends JdbcRepositoryNG<User> implements UserRepository {
    public static final String REMOVE_CURRENTWORLDID_SQL = "update users set currentWorldId=null where currentWorldId=?";

    private final DataSource dataSource;

    @Inject
    public JdbcUserRepository(Table<User> table, Worker worker, DataSource dataSource) {
        super(table, worker);
        this.dataSource = dataSource;
    }

    @Override
    protected User pipelineUpdate(User user) {
        user.setUpdatedAt(Instant.now());
        return user;
    }

    @SneakyThrows
    @Override
    protected Connection getConnection() {
        return dataSource.getConnection();
    }

    @Override
    public Future<User> find(int id) {
        return findFirstByIntColumn("id", id);
    }

    @Override
    public Future<User> findByUsername(String username) {
        return findFirstByColumn("username", username);
    }

    @Override
    public Future<User> save(User user) {
        return update(user);
    }

    @Override
    public Future<Unit> removeCurrentWorldId(int worldId) {
        return getWorker().cast(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(REMOVE_CURRENTWORLDID_SQL))
            {
                statement.setInt(1, worldId);
                statement.executeUpdate();
            }
        });
    }
}
