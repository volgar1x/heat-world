package org.heat.world.players.shortcuts;

import com.google.common.collect.ImmutableList;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.shared.database.NamedPreparedStatement;
import org.heat.shared.database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class PlayerShortcutTable implements Table<PlayerShortcut> {
    @Override
    public String getTableName() {
        return "player_shortcuts";
    }

    @Override
    public List<String> getPrimaryKeys() {
        return ImmutableList.of("player_id", "slot", "bar_type");
    }

    @Override
    public List<String> getInsertableColumns() {
        return ImmutableList.of(
                "type",
                "item_uid",
                "spell_id"
        );
    }

    @Override
    public void setPrimaryKeys(NamedPreparedStatement s, PlayerShortcut val) throws SQLException {
        s.setInt("player_id", val.getPlayerId());
        s.setInt("slot", val.getSlot());
        s.setByte("bar_type", val.getBarType().value);
    }

    @Override
    public void insertToDb(NamedPreparedStatement s, PlayerShortcut val) throws SQLException {
        if (val instanceof ItemShortcut) {
            s.setString("type", "item");
            s.setInt("item_uid", ((ItemShortcut) val).getItemUid());
        } else if (val instanceof SpellShortcut) {
            s.setString("type", "spell");
            s.setInt("spell_id", ((SpellShortcut) val).getSpellId());
        }
    }

    @Override
    public Future<PlayerShortcut> importFromDb(ResultSet rset) throws SQLException {
        PlayerShortcut shortcut;

        shortcut = // no need here to verify if shortcut == null
                tryImportItemShortcut(rset);

        shortcut = shortcut != null ? shortcut :
                tryImportSpellShortcut(rset);

        if (shortcut == null) {
            throw new IllegalArgumentException();
        }
        return Futures.success(shortcut);
    }

    ItemShortcut tryImportItemShortcut(ResultSet resultSet) throws SQLException {
        if (resultSet.getString("type").equals("item")) {
            return new ItemShortcut(
                    resultSet.getInt("player_id"),
                    resultSet.getInt("slot"),
                    resultSet.getInt("item_uid"),
                    resultSet.getInt("item_gid")
            );
        }
        return null;
    }

    SpellShortcut tryImportSpellShortcut(ResultSet resultSet) throws SQLException {
        if (resultSet.getString("type").equals("spell")) {
            return new SpellShortcut(
                    resultSet.getInt("player_id"),
                    resultSet.getInt("slot"),
                    resultSet.getShort("spell_id")
            );
        }
        return null;
    }

    @Override
    public List<String> getSelectableColumns() {
        throw new UnsupportedOperationException(); // never used
    }

    @Override
    public List<String> getUpdatableColumns() {
        throw new UnsupportedOperationException(); // never used
    }

    @Override
    public void updateToDb(NamedPreparedStatement s, PlayerShortcut val) throws SQLException {
        throw new UnsupportedOperationException(); // not used
    }
}
