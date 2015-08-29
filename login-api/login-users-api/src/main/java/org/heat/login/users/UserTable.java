package org.heat.login.users;

import com.google.common.collect.ImmutableList;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.User;
import org.heat.UserRank;
import org.heat.shared.database.NamedPreparedStatement;
import org.heat.shared.database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public final class UserTable implements Table<User> {
    @Override
    public String getTableName() {
        return "users";
    }

    @Override
    public List<String> getPrimaryKeys() {
        return ImmutableList.of("id");
    }

    @Override
    public List<String> getSelectableColumns() {
        return ImmutableList.of(
                "username",
                "nickname",
                "salt",
                "hashpass",
                "communityId",
                "secretQuestion",
                "secretAnswer",
                "rank",
                "subscriptionEnd",
                "banEnd",
                "connected",
                "createdAt",
                "lastServerId",
                "currentWorldId",
                "updatedAt"
        );
    }

    @Override
    public List<String> getInsertableColumns() {
        return ImmutableList.of(); // users never get inserted
    }

    @Override
    public List<String> getUpdatableColumns() {
        return ImmutableList.of(
                "rank",
                "subscriptionEnd",
                "banEnd",
                "connected",
                "lastServerId",
                "currentWorldId",
                "updatedAt"
        );
    }

    @Override
    public void setPrimaryKeys(NamedPreparedStatement s, User val) throws SQLException {
        s.setInt("id", val.getId());
    }

    @Override
    public Future<User> importFromDb(ResultSet rset) throws SQLException {
        User user = new User();
        user.setId(rset.getInt("id"));
        user.setUsername(rset.getString("username"));
        user.setNickname(rset.getString("nickname"));
        user.setSalt(rset.getString("salt"));
        user.setHashpass(rset.getString("hashpass"));
        user.setCommunityId((byte) rset.getInt("communityId"));
        user.setSecretQuestion(rset.getString("secretQuestion"));
        user.setSecretAnswer(rset.getString("secretAnswer"));
        user.setRank(UserRank.values()[rset.getInt("rank")]);
        user.setSubscriptionEnd(Optional.ofNullable(rset.getTimestamp("subscriptionEnd")).map(Timestamp::toInstant));
        user.setBanEnd(Optional.ofNullable(rset.getTimestamp("banEnd")).map(Timestamp::toInstant));
        user.setConnected(rset.getBoolean("connected"));
        user.setCreatedAt(rset.getTimestamp("createdAt").toInstant());
        int lastServerId = rset.getInt("lastServerId");
        if (!rset.wasNull()) {
            user.setLastServerId(OptionalInt.of(lastServerId));
        } else {
            user.setLastServerId(OptionalInt.empty());
        }
        int currentWorldId = rset.getInt("currentWorldId");
        if (!rset.wasNull()) {
            user.setCurrentWorldId(OptionalInt.of(currentWorldId));
        } else {
            user.setCurrentWorldId(OptionalInt.empty());
        }
        user.setUpdatedAt(rset.getTimestamp("updatedAt").toInstant());
        return Futures.success(user);
    }

    @Override
    public void insertToDb(NamedPreparedStatement s, User val) throws SQLException {
        // users never get inserted
    }

    @Override
    public void updateToDb(NamedPreparedStatement s, User user) throws SQLException {
        s.setInt("rank", user.getRank().ordinal());
        s.setBoolean("connected", user.isConnected());
        s.setTimestamp("updatedAt", Timestamp.from(user.getUpdatedAt()));

        if (user.getSubscriptionEnd().isPresent()) {
            s.setTimestamp("subscriptionEnd", Timestamp.from(user.getSubscriptionEnd().get()));
        } else {
            s.setNull("subscriptionEnd", Types.TIMESTAMP);
        }

        if (user.getBanEnd().isPresent()) {
            s.setTimestamp("banEnd", Timestamp.from(user.getBanEnd().get()));
        } else {
            s.setNull("banEnd", Types.TIMESTAMP);
        }

        if (user.getLastServerId().isPresent()) {
            s.setInt("lastServerId", user.getLastServerId().getAsInt());
        } else {
            s.setNull("lastServerId", Types.INTEGER);
        }

        if (user.getCurrentWorldId().isPresent()) {
            s.setInt("currentWorldId", user.getCurrentWorldId().getAsInt());
        } else {
            s.setNull("currentWorldId", Types.INTEGER);
        }
    }
}
