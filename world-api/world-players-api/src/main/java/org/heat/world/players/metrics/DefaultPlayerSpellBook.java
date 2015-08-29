package org.heat.world.players.metrics;

import com.ankamagames.dofus.datacenter.breeds.Breed;
import org.heat.datacenter.Datacenter;
import org.heat.world.players.Players;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class DefaultPlayerSpellBook implements PlayerSpellBook {

    private final List<PlayerSpell> spells;

    private DefaultPlayerSpellBook(List<PlayerSpell> spells) {
        this.spells = spells;
    }

    public static DefaultPlayerSpellBook create(List<PlayerSpell> spells) {
        return new DefaultPlayerSpellBook(new ArrayList<>(spells));
    }

    public static DefaultPlayerSpellBook create(Datacenter datacenter, Breed breed, int actualLevel) {
        return new DefaultPlayerSpellBook(Players.buildDefaultBreedSpells(datacenter, breed, actualLevel));
    }

    @Override
    public Stream<PlayerSpell> getSpellStream() {
        return spells.stream();
    }
}
