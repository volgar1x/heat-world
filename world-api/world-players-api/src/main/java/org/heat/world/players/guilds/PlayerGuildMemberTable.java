package org.heat.world.players.guilds;

import org.fungsi.concurrent.Future;
import org.heat.shared.database.NamedPreparedStatement;
import org.heat.shared.database.Table;
import org.heat.world.guilds.WorldGuildPermissions;
import org.heat.world.guilds.WorldGuildRepository;
import org.heat.world.players.Player;
import org.heat.world.players.PlayerRepository;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public final class PlayerGuildMemberTable implements Table<Player> {
    private final PlayerRepository players;
    private final WorldGuildRepository guilds;

    @Inject
    public PlayerGuildMemberTable(PlayerRepository players, WorldGuildRepository guilds) {
        this.players = players;
        this.guilds = guilds;
    }

    @Override
    public String getTableName() {
        return "guild_members";
    }

    @Override
    public List<String> getPrimaryKeys() {
        return Arrays.asList("guild_id", "player_id");
    }

    @Override
    public List<String> getSelectableColumns() {
        return Arrays.asList("permissions", "rank", "given_experience_percent");
    }

    @Override
    public List<String> getInsertableColumns() {
        return getSelectableColumns();
    }

    @Override
    public List<String> getUpdatableColumns() {
        return getInsertableColumns();
    }

    @Override
    public void setPrimaryKeys(NamedPreparedStatement s, Player val) throws SQLException {
        s.setInt("guild_id", val.getGuild().getId());
        s.setInt("player_id", val.getId());
    }

    @Override
    public Future<Player> importFromDb(ResultSet rset) throws SQLException {
        int playerId = rset.getInt("player_id");
//        int guildId = rset.getInt("guild_id");
        WorldGuildPermissions permissions = WorldGuildPermissions.of(rset.getInt("permissions"));
        short rank = rset.getShort("rank");
        byte givenExperiencePercent = rset.getByte("given_experience_percent");

        return players.find(playerId)
                .map(player -> {
                    player.setGuildPermissions(permissions);
                    player.setGuildRank(rank);
                    player.setGuildGivenExperiencePercent(givenExperiencePercent);
                    return player;
                });
    }

    @Override
    public void insertToDb(NamedPreparedStatement s, Player val) throws SQLException {
        s.setInt("permissions", val.getGuildPermissions().getBits());
        s.setShort("rank", val.getGuildRank());
        s.setByte("given_experience_percent", val.getGuildGivenExperiencePercent());
    }

    @Override
    public void updateToDb(NamedPreparedStatement s, Player val) throws SQLException {
        insertToDb(s, val);
    }
}
