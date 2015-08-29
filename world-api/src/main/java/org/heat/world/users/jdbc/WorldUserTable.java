package org.heat.world.users.jdbc;

import com.github.blackrush.acara.EventBus;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.shared.database.NamedPreparedStatement;
import org.heat.shared.database.Table;
import org.heat.world.users.WorldUser;

import javax.inject.Inject;
import javax.inject.Provider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
public class WorldUserTable implements Table<WorldUser> {
    @Inject Provider<EventBus> eventBus;

    @Override
    public String getTableName() {
        return "world_users";
    }

    @Override
    public List<String> getPrimaryKeys() {
        return ImmutableList.of("id");
    }

    @Override
    public List<String> getSelectableColumns() {
        return ImmutableList.of("channels", "nickname", "lastConnection", "listeningfriends");
    }

    @Override
    public List<String> getInsertableColumns() {
        return ImmutableList.of("channels", "nickname", "lastConnection",
                                "listeningFriends");
    }

    @Override
    public List<String> getUpdatableColumns() {
        return ImmutableList.of("channels", "lastConnection", "listeningFriends");
    }

    @Override
    public void setPrimaryKeys(NamedPreparedStatement s, WorldUser val) throws SQLException {
        s.setInt("id", val.getId());
    }

    @Override
    public Future<WorldUser> importFromDb(ResultSet rset) throws SQLException {
        WorldUser user = new WorldUser(eventBus.get());
        user.setId(rset.getInt("id"));
        user.setNickname(rset.getString("nickname"));
        user.setChannels(rset.getInt("channels"));
        user.setLastConnection(rset.getTimestamp("lastConnection").toInstant());
        user.setListeningFriends(rset.getBoolean("listeningFriends"));

        return Futures.success(user);
    }

    @Override
    public void insertToDb(NamedPreparedStatement s, WorldUser val) throws SQLException {
        s.setString("nickname", val.getNickname());
        updateToDb(s, val);
    }

    @Override
    public void updateToDb(NamedPreparedStatement s, WorldUser val) throws SQLException {
        s.setInt("channels", val.getChannels());
        s.setTimestamp("lastConnection", Timestamp.from(val.getLastConnection()));
        s.setBoolean("listeningFriends", val.isListeningFriends());
    }
}
