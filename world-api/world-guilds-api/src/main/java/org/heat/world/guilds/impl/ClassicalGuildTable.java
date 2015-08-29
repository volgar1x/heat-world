package org.heat.world.guilds.impl;

import com.ankamagames.dofus.network.types.game.guild.GuildEmblem;
import com.github.blackrush.acara.EventBus;
import com.typesafe.config.Config;
import org.fungsi.concurrent.Future;
import org.heat.shared.database.NamedPreparedStatement;
import org.heat.shared.database.Table;
import org.heat.world.guilds.WorldGuild;
import org.heat.world.guilds.WorldGuildMemberRepository;

import javax.inject.Inject;
import javax.inject.Provider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class ClassicalGuildTable implements Table<WorldGuild> {
    private final Provider<EventBus> eventBusProvider;
    private final WorldGuildMemberRepository guildMembers;
    private final byte initialGivenExperiencePercent;

    @Inject
    ClassicalGuildTable(Provider<EventBus> eventBusProvider, WorldGuildMemberRepository guildMembers, Config config) {
        this.eventBusProvider = eventBusProvider;
        this.guildMembers = guildMembers;
        this.initialGivenExperiencePercent = (byte)config.getInt("heat.world.guilds.initial-given-experience-percent");
    }

    @Override
    public String getTableName() {
        return "guilds";
    }

    @Override
    public List<String> getPrimaryKeys() {
        return Collections.singletonList("id");
    }

    @Override
    public List<String> getSelectableColumns() {
        return Arrays.asList("name",
                             "emblem_background_id", "emblem_background_color",
                             "emblem_foreground_id", "emblem_foreground_color");
    }

    @Override
    public List<String> getInsertableColumns() {
        return getSelectableColumns();
    }

    @Override
    public List<String> getUpdatableColumns() {
        return Collections.emptyList();
    }

    @Override
    public void setPrimaryKeys(NamedPreparedStatement s, WorldGuild val) throws SQLException {
        s.setInt("id", val.getId());
    }

    @Override
    public Future<WorldGuild> importFromDb(ResultSet rset) throws SQLException {
        ClassicalGuild guild = new ClassicalGuild(
                eventBusProvider.get(), guildMembers, initialGivenExperiencePercent,
                rset.getInt("id"),
                rset.getString("name"),
                new GuildEmblem(rset.getShort("emblem_foreground_id"),
                                rset.getInt("emblem_foreground_color"),
                                rset.getShort("emblem_background_id"),
                                rset.getInt("emblem_background_color"))
        );

        return guildMembers.findByGuild(guild)
                .onSuccess(guild::checkin)
                .map(members -> guild);
    }

    @Override
    public void insertToDb(NamedPreparedStatement s, WorldGuild val) throws SQLException {
        s.setString("name", val.getName());
        s.setShort("emblem_foreground_id", val.getEmblem().symbolShape);
        s.setInt("emblem_foreground_color", val.getEmblem().symbolColor);
        s.setShort("emblem_background_id", val.getEmblem().backgroundShape);
        s.setInt("emblem_background_color", val.getEmblem().backgroundColor);
    }

    @Override
    public void updateToDb(NamedPreparedStatement s, WorldGuild val) throws SQLException {

    }
}
