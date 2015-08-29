package org.heat.world.players.metrics;

import com.ankamagames.dofus.network.types.game.data.items.SpellItem;

import java.util.Optional;
import java.util.stream.Stream;

public interface PlayerSpellBook {

    Stream<PlayerSpell> getSpellStream();

    default Optional<PlayerSpell> findById(int id) {
        return getSpellStream().filter(x -> x.getId() == id).findAny();
    }

    default Optional<PlayerSpell> findByPosition(int position) {
        return getSpellStream().filter(x -> x.hasPosition(position)).findAny();
    }

    default Stream<PlayerSpell> getWithoutPosition() {
        return getSpellStream().filter(x -> !x.hasPosition());
    }

    default void resetAll() {
        getSpellStream().forEach(PlayerSpell::resetLevel);
    }

    default Stream<SpellItem> toSpellItem() {
        return getSpellStream().map(PlayerSpell::toSpellItem);
    }
}
