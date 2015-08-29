package org.heat.world.items;

import com.ankamagames.dofus.datacenter.items.Item;
import com.ankamagames.dofus.network.enums.CharacterInventoryPositionEnum;
import com.ankamagames.dofus.network.types.game.data.items.effects.ObjectEffect;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.SneakyThrows;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.datacenter.Datacenter;
import org.heat.dofus.network.NetworkComponentFactory;
import org.heat.shared.database.NamedPreparedStatement;
import org.heat.shared.database.Table;
import org.heat.shared.io.AutoGrowingWriter;
import org.heat.shared.io.DataReader;
import org.heat.shared.io.InputStreamReader;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class WorldItemTable implements Table<WorldItem> {
    private final Datacenter datacenter;
    private final NetworkComponentFactory<ObjectEffect> effectFactory;

    @Inject
    public WorldItemTable(Datacenter datacenter, NetworkComponentFactory<ObjectEffect> effectFactory) {
        this.datacenter = datacenter;
        this.effectFactory = effectFactory;
    }

    @Override
    public String getTableName() {
        return "items";
    }

    @Override
    public List<String> getPrimaryKeys() {
        return ImmutableList.of("uid");
    }

    @Override
    public List<String> getSelectableColumns() {
        return ImmutableList.of(
                "gid",
                "effects",
                "position",
                "quantity"
        );
    }

    @Override
    public List<String> getInsertableColumns() {
        return ImmutableList.of(
                "gid",
                "effects",
                "position",
                "quantity",
                "updated_at"
        );
    }

    @Override
    public List<String> getUpdatableColumns() {
        return ImmutableList.of(
                "effects",
                "position",
                "quantity",
                "updated_at"
        );
    }

    @Override
    public void setPrimaryKeys(NamedPreparedStatement s, WorldItem val) throws SQLException {
        s.setInt("uid", val.getUid());
    }

    @Override
    public Future<WorldItem> importFromDb(ResultSet rset) throws SQLException {
        return Futures.success(WorldItem.create(
                rset.getInt("uid"),
                0,
                datacenter.find(Item.class, rset.getInt("gid")).get(),
                importEffects(rset),
                CharacterInventoryPositionEnum.valueOf(rset.getByte("position")).get(),
                rset.getInt("quantity")
        ));
    }

    @Override
    public void insertToDb(NamedPreparedStatement s, WorldItem val) throws SQLException {
        s.setInt("gid", val.getGid());
        updateToDb(s, val);
    }

    @Override
    public void updateToDb(NamedPreparedStatement s, WorldItem item) throws SQLException {
        exportEffects(s, item.getEffects());
        s.setInt("position", item.getPosition().value);
        s.setInt("quantity", item.getQuantity());
        s.setTimestamp("updated_at", new Timestamp(System.currentTimeMillis()));
    }

    @SneakyThrows
    ImmutableSet<WorldItemEffect> importEffects(ResultSet rset) {
        ImmutableSet.Builder<WorldItemEffect> effects = ImmutableSet.builder();

        byte[] bytes = rset.getBytes("effects");
        DataReader reader = InputStreamReader.of(bytes);
        while (reader.canRead(2)) {
            int typeId = reader.read_ui16();
            ObjectEffect effect = effectFactory.create(typeId).get();
            effect.deserialize(reader);

            effects.add(Effects.fromObjectEffect(effect));
        }

        return effects.build();
    }

    @SneakyThrows
    void exportEffects(NamedPreparedStatement s, ImmutableSet<WorldItemEffect> effects) {
        AutoGrowingWriter writer = new AutoGrowingWriter();
        for (WorldItemEffect effect : effects) {
            ObjectEffect e = effect.toObjectEffect();
            writer.write_ui16(e.getProtocolId());
            e.serialize(writer);
        }

        ///////////////////////////
        // NOTE(Blackrush):
        //      pgjdbc-ng only supports a small subset of InputStream
        //      thus forbidding AutoGrowingWriter's InputStream
        //
        //s.setBinaryStream("effects", writer.toInputStream());

        s.setBytes("effects", writer.toByteArray());
    }
}
