package org.heat.world.users.jdbc;

import com.ankamagames.dofus.network.enums.ChatActivableChannelsEnum;
import com.github.blackrush.acara.EventBus;
import lombok.SneakyThrows;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.heat.User;
import org.heat.shared.database.JdbcRepositoryNG;
import org.heat.shared.database.Table;
import org.heat.world.users.UserRepository;
import org.heat.world.users.WorldUser;
import org.heat.world.users.WorldUserRepository;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("deprecation")
public final class JdbcWorldUserRepository extends JdbcRepositoryNG<WorldUser>
    implements WorldUserRepository,
           WorldUserRepository.Unsafe
{
    private final DataSource dataSource;
    private final UserRepository userRepository;
    private final Clock clock;
    private final Provider<EventBus> eventBus;

    @Inject
    public JdbcWorldUserRepository(Table<WorldUser> table, Worker worker, DataSource dataSource, UserRepository userRepository,
                                   Clock clock, Provider<EventBus> eventBus) {
        super(table, worker);
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.clock = clock;
        this.eventBus = eventBus;
    }

    @SneakyThrows
    @Override
    protected Connection getConnection() {
        return dataSource.getConnection();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Unsafe getUnsafe() {
        return this;
    }

    @SuppressWarnings("UnusedDeclaration")
    private int packChannelsAsInt(ChatActivableChannelsEnum... channels) {
        int integer = 0;
        for (ChatActivableChannelsEnum channel : channels) {
            integer |= (1 << channel.value);
        }
        return integer;
    }

    private Future<WorldUser> create(User user) {
        WorldUser wuser = new WorldUser(eventBus.get());
        wuser.setId(user.getId());
        wuser.setNickname(user.getNickname());
        wuser.setUser(user);
        //wuser.setChannels(packChannelsAsInt(CHANNEL_SALES, CHANNEL_SEEK, CHANNEL_GLOBAL, CHANNEL_PARTY, CHANNEL_GUILD));
        wuser.setChannels(Integer.MAX_VALUE);
        wuser.setLastConnection(clock.instant());

        return insert(wuser);
    }

    private Future<WorldUser> findOrCreate(User user) {
        return findFirstByIntColumn("id", user.getId())
            .map(worldUser -> {
                worldUser.setUser(user);
                return worldUser;
            })
            .mayRescue(err -> create(user));
    }

    private Future<WorldUser> load(WorldUser wuser) {
        return userRepository.find(wuser.getId())
                .map(user -> {
                    wuser.setUser(user);
                    return wuser;
                });
    }

    @Override
    public Future<WorldUser> find(int id) {
        return userRepository.find(id)
            .flatMap(this::findOrCreate);
    }

    @Override
    public Future<WorldUser> findOrRefresh(int id, Instant updatedAt) {
        return userRepository.findOrRefresh(id, updatedAt)
            .<WorldUser>flatMap(this::findOrCreate);
    }

    @Override
    public Future<List<WorldUser>> findMany(int[] ids) {
        List<Future<WorldUser>> futures = new LinkedList<>();
        for (int id : ids) {
            futures.add(find(id));
        }
        return Futures.collect(futures);
    }

    @Override
    public Future<WorldUser> findByNickname(String nickname) {
        return findFirstByColumn("nickname", nickname)
                .flatMap(this::load);
    }

    @Override
    public Future<Unit> save(WorldUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<Boolean> isPresent(WorldUser user) {
        return getWorker().submit(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("select 1 from world_users where id=?")) {
                statement.setInt(1, user.getId());

                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        });
    }

    @Override
    public Future<WorldUser> update(WorldUser val) {
        return super.update(val);
    }

    @Override
    public Future<WorldUser> insert(WorldUser val) {
        return super.insert(val);
    }
}
