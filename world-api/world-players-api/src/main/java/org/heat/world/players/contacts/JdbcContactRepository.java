package org.heat.world.players.contacts;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Worker;
import org.fungsi.function.UnsafeConsumer;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

final class JdbcContactRepository implements ContactRepository {
    private final DataSource source;
    private final Worker worker;

    @Inject
    public JdbcContactRepository(DataSource source, Worker worker) {
        this.source = source;
        this.worker = worker;
    }

    @Override
    public Future<Unit> link(int fromId, int toId, Kind kind) {
        return write("insert into contacts(from_id, to_id, kind) values(?, ?, ?)", s -> {
            s.setInt(1, fromId);
            s.setInt(2, toId);
            s.setString(3, kind.name().toLowerCase());
        });
    }

    @Override
    public Future<Unit> unlink(int fromId, int toId) {
        return write("delete from contacts where from_id=? and to_id=?", s -> {
            s.setInt(1, fromId);
            s.setInt(2, toId);
        });
    }

    @Override
    public Future<int[]> getLinksFrom(int fromId, Kind kind) {
        return readInts("select to_id from contacts where from_id=? and kind=?", s -> {
            s.setInt(1, fromId);
            s.setString(2, kind.name().toLowerCase());
        });
    }

    private Future<Unit> write(String sql, UnsafeConsumer<PreparedStatement> fn) {
        return worker.cast(() -> {
            try (Connection connection = source.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                fn.accept(stmt);

                stmt.executeUpdate();
            }
        });
    }

    private Future<int[]> readInts(String sql, UnsafeConsumer<PreparedStatement> fn) {
        return worker.submit(() -> {
            final int[] ids;
            try (Connection connection = source.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                fn.accept(stmt);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (!rset.last()) {
                        ids = new int[0];
                    } else {
                        ids = new int[rset.getRow()];
                        rset.beforeFirst();
                        for (int i = 0; rset.next(); i++) {
                            ids[i] = rset.getInt(1);
                        }
                    }
                }
            }
            return ids;
        });
    }
}
