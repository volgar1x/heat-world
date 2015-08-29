package org.heat.world.players.guilds;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.heat.shared.database.Connections;
import org.heat.shared.database.NamedPreparedStatement;
import org.heat.shared.database.Table;
import org.heat.shared.database.Tables;
import org.heat.world.guilds.*;
import org.heat.world.players.Player;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public final class PlayerGuildMemberRepository implements WorldGuildMemberRepository {
    private final Worker        worker;
    private final DataSource    dataSource;
    private final Table<Player> table;

    @Inject
    public PlayerGuildMemberRepository(Worker worker, DataSource dataSource, Table<Player> table) {
        this.worker = worker;
        this.dataSource = dataSource;
        this.table = table;
    }

    @Override
    public Future<List<? extends WorldGuildMember>> findByGuild(WorldGuild guild) {
        String query = Tables.createSelectQuery(table) + " where guild_id=?";

        return worker.execute(() -> {
            List<Future<Player>> players = new LinkedList<>();
            try (Connection co = dataSource.getConnection();
                 PreparedStatement ps = co.prepareStatement(query)) {
                ps.setInt(1, guild.getId());

                try (ResultSet rset = ps.executeQuery()) {
                    while (rset.next()) {
                        Future<Player> player = table.importFromDb(rset).map(p -> {
                            p.setGuild(guild);
                            return p;
                        });
                        players.add(player);
                    }
                }
            }

            @SuppressWarnings("unchecked") Future<List<? extends WorldGuildMember>> tmp = (Future) Futures
                    .collect(players);
            return tmp;
        });
    }

    @Override
    public Future<Integer> findGuildOfMemberById(int memberId) {
        String sql = "select guild_id from guild_members where player_id=?";

        return worker.submit(() -> {
            final int guildId;

            try (Connection co = dataSource.getConnection();
                 PreparedStatement s = co.prepareStatement(sql)) {
                s.setInt(1, memberId);

                try (ResultSet rset = s.executeQuery()) {
                    if (!rset.next()) {
                        throw new NoSuchElementException();
                    }
                    guildId = rset.getInt(1);
                }
            }

            return guildId;
        });
    }

    @Override
    public Future<Unit> save(WorldGuildMember member) {
        Player player = (Player) member;

        return worker.cast(() -> {
            try (Connection co = dataSource.getConnection()) {
                if (isPersisted(co, player)) {
                    update(co, player);
                } else {
                    insert(co, player);
                }
            }
        });
    }

    @Override
    public Future<Unit> remove(WorldGuildMember member) {
        Player player = (Player) member;

        player.setGuild(null);
        player.setGuildPermissions(WorldGuildPermissions.NONE);
        player.setGuildRank(WorldGuildRanks.ON_TRIAL.id);
        player.setGuildGivenExperiencePercent((byte) 0);

        return worker.cast(() -> {
            String sql = "delete from guild_members where player_id=?";

            try (Connection co = dataSource.getConnection();
                 PreparedStatement s = co.prepareStatement(sql)) {
                s.setInt(1, player.getId());
                s.executeUpdate();
            }
        });
    }

    private boolean isPersisted(Connection co, Player player) throws SQLException {
        String sql = "select count(player_id) from guild_members where player_id=?";

        try (PreparedStatement s = co.prepareStatement(sql)) {
            s.setInt(1, player.getId());

            try (ResultSet rset = s.executeQuery()) {
                return rset.next() && rset.getInt(1) > 0;
            }
        }
    }

    private void update(Connection co, Player player) throws SQLException {
        String sql = "update guild_members set " +
                        "guild_id=:guild_id, " +
                        "permissions=:permissions, " +
                        "rank=:rank, " +
                        "given_experience_percent=:given_experience_percent " +
                     "where player_id=:player_id";

        try (NamedPreparedStatement s = Connections.prepare(co, sql)) {
            table.setPrimaryKeys(s, player);
            table.updateToDb(s, player);

            s.execute();
        }
    }

    private void insert(Connection co, Player player) throws SQLException {
        String sql = Tables.createInsertQuery(table);

        try (NamedPreparedStatement s = Connections.prepare(co, sql)) {
            table.setPrimaryKeys(s, player);
            table.insertToDb(s, player);

            s.execute();
        }
    }
}
