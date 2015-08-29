package org.heat.world.players.shortcuts;

import com.ankamagames.dofus.network.enums.ShortcutBarEnum;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.fungsi.Either;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.heat.shared.Pair;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LazyShortcutBar implements PlayerShortcutBar {
    private final PlayerShortcutRepository repository;
    private final int playerId;

    private Multimap<ShortcutBarEnum, PlayerShortcut> shortcuts;

    public LazyShortcutBar(PlayerShortcutRepository repository, int playerId) {
        this.repository = repository;
        this.playerId = playerId;
    }

    private ShortcutBarEnum getShortcutType(PlayerShortcut shortcut) {
        return shortcut.getBarType();
    }

    private Multimap<ShortcutBarEnum, PlayerShortcut> load() {
        // TODO(world/players): shortcut load timeout
        List<PlayerShortcut> loaded = repository.findAll(playerId).get();

        Multimap<ShortcutBarEnum, PlayerShortcut> shortcuts = HashMultimap.create();

        for (PlayerShortcut shortcut : loaded) {
            ShortcutBarEnum bar = getShortcutType(shortcut);

            shortcuts.put(bar, shortcut);
        }

        return shortcuts;
    }

    private void requireLoaded() {
        // might need synchronization
        if (shortcuts == null) {
            shortcuts = load();
        }
    }

    private Stream<PlayerShortcut> getShortcutsOf0(ShortcutBarEnum bar) {
        return shortcuts.get(bar).stream();
    }

    private void add0(PlayerShortcut shortcut) {
        shortcuts.put(shortcut.getBarType(), shortcut);
        repository.create(shortcut);
    }

    private Future<Unit> remove0(PlayerShortcut shortcut) {
        shortcuts.remove(shortcut.getBarType(), shortcut);
        return repository.remove(shortcut).toUnit();
    }

    private Pair<PlayerShortcut, PlayerShortcut> swap0(PlayerShortcut from, PlayerShortcut to) {
        PlayerShortcut newFrom = from.withSlot(to.getSlot());
        PlayerShortcut newTo = to.withSlot(from.getSlot());

        remove0(from)
        .flatMap(x -> remove0(to))
                .onSuccess(u -> {
                    add0(newFrom);
                    add0(newTo);
                });

        return Pair.of(newFrom, newTo);
    }

    private PlayerShortcut move0(PlayerShortcut shortcut, int newSlot) {
        PlayerShortcut newShortcut = shortcut.withSlot(newSlot);

        remove0(shortcut);
        add0(newShortcut);

        return newShortcut;
    }

    @Override
    public Stream<PlayerShortcut> getShortcutsOf(ShortcutBarEnum bar) {
        requireLoaded();
        return getShortcutsOf0(bar);
    }

    @Override
    public Optional<PlayerShortcut> findShortcut(ShortcutBarEnum bar, int slot) {
        requireLoaded();
        return getShortcutsOf0(bar).filter(x -> x.getSlot() == slot).findAny();
    }

    @Override
    public boolean add(PlayerShortcut shortcut) {
        requireLoaded();

        ShortcutBarEnum bar = getShortcutType(shortcut);
        boolean alreadyTaken = getShortcutsOf0(bar).anyMatch(x -> x.getSlot() == shortcut.getSlot());

        if (!alreadyTaken) {
            add0(shortcut);
        }

        return alreadyTaken;
    }

    @Override
    public boolean remove(ShortcutBarEnum bar, int slot) {
        requireLoaded();

        Optional<PlayerShortcut> option = findShortcut(bar, slot);
        if (option.isPresent()) {
            PlayerShortcut shortcut = option.get();
            remove0(shortcut);
        }

        return option.isPresent();
    }

    @Override
    public Either<PlayerShortcut, Pair<PlayerShortcut, PlayerShortcut>> swap(ShortcutBarEnum bar, int fromSlot, int toSlot) {
        requireLoaded();

        PlayerShortcut from = findShortcut(bar, fromSlot).get();
        Optional<PlayerShortcut> toOption = findShortcut(bar, toSlot);

        if (toOption.isPresent()) {
            return Either.right(swap0(from, toOption.get()));
        } else {
            return Either.left(move0(from, toSlot));
        }
    }

    @Override
    public List<PlayerShortcut> removeItemShortcut(int itemUid) {
        requireLoaded();

        List<PlayerShortcut> result =
            getShortcutsOf0(ItemShortcut.BAR_TYPE)
                .filter(x -> x instanceof ItemShortcut && ((ItemShortcut) x).getItemUid() == itemUid)
                .collect(Collectors.toList());

        result.forEach(this::remove0);

        return result;
    }
}
