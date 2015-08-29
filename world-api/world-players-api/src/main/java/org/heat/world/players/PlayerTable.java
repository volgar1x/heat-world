package org.heat.world.players;

import com.ankamagames.dofus.datacenter.breeds.Breed;
import com.ankamagames.dofus.network.enums.DirectionsEnum;
import com.ankamagames.dofus.network.types.game.character.status.PlayerStatusExtended;
import com.github.blackrush.acara.EventBus;
import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import org.fungsi.concurrent.Future;
import org.heat.datacenter.Datacenter;
import org.heat.shared.database.NamedPreparedStatement;
import org.heat.shared.database.Table;
import org.heat.world.items.MapItemBag;
import org.heat.world.metrics.Experience;
import org.heat.world.players.contacts.ContactListFactory;
import org.heat.world.players.items.LazyPlayerItemWallet;
import org.heat.world.players.items.PlayerItemRepository;
import org.heat.world.players.items.PlayerItemWallet;
import org.heat.world.players.metrics.*;
import org.heat.world.players.shortcuts.LazyShortcutBar;
import org.heat.world.players.shortcuts.PlayerShortcutBar;
import org.heat.world.players.shortcuts.PlayerShortcutRepository;
import org.heat.world.roleplay.WorldActorLook;
import org.heat.world.roleplay.environment.WorldMapPoint;
import org.heat.world.roleplay.environment.WorldPositioningSystem;
import org.heat.world.users.WorldUserRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import static org.heat.world.metrics.GameStats.*;

public final class PlayerTable implements Table<Player> {
    private final Datacenter datacenter;
    private final WorldPositioningSystem wps;
    private final Experience experience;
    private final PlayerItemRepository playerItems;
    private final PlayerShortcutRepository playerShortcuts;
    private final Provider<EventBus> eventBusProvider;
    private final WorldUserRepository userRepository;
    private final ContactListFactory contactList;

    @Inject
    public PlayerTable(
            Datacenter datacenter,
            WorldPositioningSystem wps,
            @Named("player") Experience experience,
            PlayerItemRepository playerItems,
            PlayerShortcutRepository playerShortcuts,
            @Named("player") Provider<EventBus> eventBusProvider,
            WorldUserRepository userRepository,
            ContactListFactory contactList) {
        this.datacenter = datacenter;
        this.wps = wps;
        this.experience = experience;
        this.playerItems = playerItems;
        this.playerShortcuts = playerShortcuts;
        this.eventBusProvider = eventBusProvider;
        this.userRepository = userRepository;
        this.contactList = contactList;
    }

    @Override
    public String getTableName() {
        return "players";
    }

    @Override
    public List<String> getPrimaryKeys() {
        return ImmutableList.of("id");
    }

    @Override
    public List<String> getSelectableColumns() {
        return ImmutableList.of(
                "last_used_at",
                "userId",
                "name",
                "breedId",
                "sex",
                "lookId",
                "headId",
                "scale",
                "colors",
                "mapId",
                "cellId",
                "directionId",
                "experience",
                "statsPoints",
                "spellsPoints",
                "strength",
                "vitality",
                "wisdom",
                "chance",
                "agility",
                "intelligence",
                "life",
                "energy",
                "maxEnergy",
                "actions",
                "movements",
                "prospecting",
                "summonableCreatures",
                "spells",
                "kamas"
        );
    }

    @Override
    public List<String> getInsertableColumns() {
        return ImmutableList.of(
                "updated_at",
                "last_used_at",
                "userId",
                "name",
                "breedId",
                "sex",
                "lookId",
                "headId",
                "scale",
                "colors",
                "mapId",
                "cellId",
                "directionId",
                "experience",
                "statsPoints",
                "spellsPoints",
                "strength",
                "vitality",
                "wisdom",
                "chance",
                "agility",
                "intelligence",
                "life",
                "energy",
                "maxEnergy",
                "actions",
                "movements",
                "prospecting",
                "summonableCreatures",
                "spells",
                "kamas",
                "status"
        );
    }

    @Override
    public List<String> getUpdatableColumns() {
        return ImmutableList.of(
                "updated_at",
                "sex",
                "lookId",
                "headId",
                "scale",
                "colors",
                "mapId",
                "cellId",
                "directionId",
                "experience",
                "statsPoints",
                "spellsPoints",
                "strength",
                "vitality",
                "wisdom",
                "chance",
                "agility",
                "intelligence",
                "life",
                "energy",
                "maxEnergy",
                "actions",
                "movements",
                "prospecting",
                "summonableCreatures",
                "spells",
                "kamas",
                "status"
        );
    }

    @Override
    public void setPrimaryKeys(NamedPreparedStatement s, Player val) throws SQLException {
        s.setInt("id", val.getId());
    }



    @Override
    public Future<Player> importFromDb(ResultSet rset) throws SQLException {
        Player player = new Player();
        player.setEventBus(eventBusProvider.get());
        player.setId(rset.getInt("id"));
        player.setName(rset.getString("name"));
        player.setBreed(datacenter.find(Breed.class, rset.getInt("breedId")).get());
        player.setSex(rset.getBoolean("sex"));
        player.setPosition(wps.locate(
                rset.getInt("mapId"),
                WorldMapPoint.of(rset.getInt("cellId")).get(),
                DirectionsEnum.valueOf(rset.getByte("directionId")).get()
        ));
        player.setExperience(buildPlayerExperience(rset.getDouble("experience")));
        player.setStats(buildPlayerStats(player.getBreed(), rset));
        player.setSpells(buildPlayerSpells(rset));
        player.setWallet(buildWallet(rset, player.getId()));
        player.setShortcutBar(buildShortcutBar(player.getId()));
        player.setLastUsedAt(rset.getTimestamp("last_used_at").toInstant());

        player.getStats().apply(player.getWallet().findEquiped());

        player.setLook(WorldActorLook.create(
                WorldActorLook.STANDING_BONES,
                rset.getShort("lookId"),
                rset.getShort("headId"),
                rset.getShort("scale"),
                asIntArray(rset.getArray("colors")),
                player.getWallet().findEquiped()));


        return userRepository.find(rset.getInt("userId"))
            .flatMap(user -> {
                player.setUser(user);

                return contactList.build(user.getId())
                        .map(contacts -> {
                            player.setContacts(contacts);

                            return player;
                        });
            });
    }

    @Override
    public void insertToDb(NamedPreparedStatement s, Player val) throws SQLException {
        s.setInt("userId", val.getUserId());
        s.setString("name", val.getName());
        s.setInt("breedId", val.getBreed().getId());
        updateToDb(s, val);
    }

    @Override
    public void updateToDb(NamedPreparedStatement s, Player player) throws SQLException {
        s.setTimestamp("updated_at", new Timestamp(System.currentTimeMillis()));
        s.setTimestamp("last_used_at", Timestamp.from(player.getLastUsedAt()));
        s.setBoolean("sex", player.getSex());
        s.setInt("lookId", player.getLook().getLookId());
        s.setInt("headId", player.getLook().getHeadId());
        s.setInt("scale", player.getLook().getScale());
        s.setObject("colors", player.getLook().getColors());
        s.setInt("mapId", player.getPosition().getMapId());
        s.setInt("cellId", player.getPosition().getCellId());
        s.setInt("directionId", player.getPosition().getDirection().value);
        s.setDouble("experience", player.getExperience().getCurrent());
        s.setInt("statsPoints", player.getStats().getStatsPoints());
        s.setInt("spellsPoints", player.getStats().getSpellsPoints());
        s.setShort("strength", player.getStats().get(STRENGTH).getBase());
        s.setShort("vitality", player.getStats().get(VITALITY).getBase());
        s.setShort("wisdom", player.getStats().get(WISDOM).getBase());
        s.setShort("chance", player.getStats().get(CHANCE).getBase());
        s.setShort("agility", player.getStats().get(AGILITY).getBase());
        s.setShort("intelligence", player.getStats().get(INTELLIGENCE).getBase());
        s.setShort("life", player.getStats().get(LIFE).getCurrent());
        s.setShort("energy", player.getStats().get(ENERGY).getCurrent());
        s.setShort("maxEnergy", player.getStats().get(ENERGY).getMax());
        s.setShort("actions", player.getStats().get(ACTIONS).getBase());
        s.setShort("movements", player.getStats().get(MOVEMENTS).getBase());
        s.setShort("prospecting", player.getStats().get(PROSPECTING).getBase());
        s.setShort("summonableCreatures", player.getStats().get(SUMMONABLE_CREATURES).getBase());
        s.setArray("spells", exportPlayerSpells(s.getConnection(), player.getSpells()));
        s.setInt("kamas", player.getWallet().getKamas());
        s.setByte("status", player.getStatus().statusId);
        if (player.getStatus() instanceof PlayerStatusExtended) {
            s.setString("status_msg", ((PlayerStatusExtended) player.getStatus()).message);
        } else {
            s.setNull("status_msg", Types.VARCHAR);
        }
    }
    private PlayerExperience buildPlayerExperience(double experience) {
        Experience step = this.experience.getNextUntilEnoughExperience(experience);
        return new PlayerExperience(experience, step);
    }

    private PlayerStatBook buildPlayerStats(Breed breed, ResultSet rset) throws SQLException {
        PlayerStatBook stats = new DefaultPlayerStatBook(
                breed,
                rset.getInt("statsPoints"),
                rset.getInt("spellsPoints"),
                rset.getShort("strength"),
                rset.getShort("vitality"),
                rset.getShort("wisdom"),
                rset.getShort("chance"),
                rset.getShort("agility"),
                rset.getShort("intelligence")
        );
        stats.get(LIFE).setCurrentAndMax(rset.getShort("life"));
        stats.get(ENERGY).setCurrentAndMax(rset.getShort("energy"), rset.getShort("maxEnergy"));
        stats.get(ACTIONS).setBase(rset.getShort("actions"));
        stats.get(MOVEMENTS).setBase(rset.getShort("movements"));
        stats.get(PROSPECTING).setBase(rset.getShort("prospecting"));
        stats.get(SUMMONABLE_CREATURES).setBase(rset.getShort("summonableCreatures"));
        return stats;
    }

    @SneakyThrows
    private PlayerItemWallet buildWallet(ResultSet rset, int id) {
        return new LazyPlayerItemWallet(
                rset.getInt("kamas"),
                id,
                playerItems,
                MapItemBag::newHashMapItemBag
        );
    }

    private PlayerShortcutBar buildShortcutBar(int playerId) {
        return new LazyShortcutBar(playerShortcuts, playerId);
    }

    private PlayerSpellBook buildPlayerSpells(ResultSet rset) throws SQLException {
        List<PlayerSpell> spells = new LinkedList<>();

        Struct[] array = (Struct[]) rset.getArray("spells").getArray();
        for (Struct struct : array) {
            // extract
            Object[] attr = struct.getAttributes();
            int spellId = (Integer) attr[0];
            int spellLevelInt = (Integer) attr[1];
            int spellPositionInt = (Integer) attr[2];

            // use
            spells.add(PlayerSpell.of(datacenter, spellId, (byte) spellLevelInt, (short) spellPositionInt));
        }

        return DefaultPlayerSpellBook.create(spells);
    }

    @SneakyThrows
    private Object exportPlayerSpell(Connection co, PlayerSpell spell) {
        return co.createStruct("player_spell", new Object[] {
                spell.getId(),
                (int) spell.getLevel(),
                spell.getPosition()
        });
    }

    private Array exportPlayerSpells(Connection co, PlayerSpellBook book) throws SQLException {
        return co.createArrayOf("player_spell", book.getSpellStream()
                .map(spell -> exportPlayerSpell(co, spell))
                .toArray());
    }

    @SneakyThrows
    static int[] asIntArray(Array array) {
        try {
            Integer[] arr = (Integer[]) array.getArray();
            int[] res = new int[arr.length];
            for (int i = 0; i < arr.length; i++) {
                res[i] = arr[i];
            }
            return res;
        } finally {
            array.free();
        }
    }
}
