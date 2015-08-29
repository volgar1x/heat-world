package org.heat.world.guilds.impl;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.heat.shared.database.JdbcRepositoryNG;
import org.heat.shared.database.Table;
import org.heat.world.guilds.WorldGuild;
import org.heat.world.guilds.WorldGuildMemberRepository;
import org.heat.world.guilds.WorldGuildRepository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

final class JdbcGuildRepository extends JdbcRepositoryNG<WorldGuild> implements WorldGuildRepository {
    private final DataSource dataSource;
    private final WorldGuildMemberRepository members;

    private final Map<Integer, WorldGuild> cache = new HashMap<>();

    @Inject
    JdbcGuildRepository(Table<WorldGuild> table, Worker worker, DataSource dataSource,
                        WorldGuildMemberRepository members) {
        super(table, worker);
        this.dataSource = dataSource;
        this.members = members;
    }

    @Override
    protected Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public Future<WorldGuild> find(int id) {
        WorldGuild guild = cache.get(id);
        if (guild != null) {
            return Futures.success(guild);
        }
        return findFirstByIntColumn("id", id)
            .onSuccess(g -> cache.put(g.getId(), g));
    }

    @Override
    public Future<WorldGuild> findByMemberId(int memberId) {
        return members.findGuildOfMemberById(memberId).flatMap(this::find);
    }

    @Override
    public Future<Unit> save(WorldGuild guild) {
        if (cache.containsKey(guild.getId())) {
//            return update(guild).toUnit();
            return Futures.unit(); // classical guilds do not need to be updated, for now?
        } else {
            cache.put(guild.getId(), guild);
            return insert(guild).toUnit();
        }
    }

    @Override
    public int getLastId() {
        try (Connection co = dataSource.getConnection();
             Statement s = co.createStatement();
             ResultSet rset = s.executeQuery("select max(id) from guilds")) {
            return rset.next() ? rset.getInt(1) : 0;
        } catch (SQLException e) {
            throw new AssertionError(e);
        }
    }
}
