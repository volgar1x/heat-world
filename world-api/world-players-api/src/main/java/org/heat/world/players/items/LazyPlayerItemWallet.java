package org.heat.world.players.items;

import org.heat.world.items.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class LazyPlayerItemWallet extends DelegateItemBag implements PlayerItemWallet {
    private final AtomicInteger kamas;
    private final int playerId;
    private final PlayerItemRepository playerItems;
    private final Supplier<WorldItemBag> bagSupplier;

    private volatile WorldItemBag bag;
    private final Object bagLock = new Object();

    public LazyPlayerItemWallet(int kamas, int playerId, PlayerItemRepository playerItems, Supplier<WorldItemBag> bagSupplier) {
        this.kamas = new AtomicInteger(kamas);
        this.playerId = playerId;
        this.playerItems = playerItems;
        this.bagSupplier = bagSupplier;
    }

    @Override
    public int getKamas() {
        return kamas.get();
    }

    @Override
    public void setKamas(int kamas) {
        this.kamas.set(kamas);
    }

    @Override
    public void plusKamas(int kamas) {
        this.kamas.getAndAdd(kamas);
    }

    private WorldItemBag loadBag() {
        // TODO(world/players): item load timeout
        List<WorldItem> items = playerItems.findItemsByPlayer(playerId).get();

        WorldItemBag bag = bagSupplier.get();
        bag.addAll(items);
        return bag;
    }

    private WorldItemBag loadBagIfNeeded() {
        // double-checked locking
        WorldItemBag result = bag;
        if (result == null) {
            synchronized (bagLock) {
                result = bag;
                if (result == null) {
                    result = bag = loadBag();
                }
            }
        }
        return result;
    }

    @Override
    protected WorldItemBag delegate() {
        return loadBagIfNeeded();
    }

    @Override
    public void add(WorldItem item) {
        super.add(item);
        playerItems.persist(playerId, item);
    }

    @Override
    public void addAll(List<WorldItem> items) {
        super.addAll(items);
        playerItems.persistAll(playerId, items.stream());
    }

    @Override
    public void remove(WorldItem item) {
        super.remove(item);
        playerItems.remove(playerId, item);
    }

    @Override
    public Optional<WorldItem> tryRemove(int uid) {
        Optional<WorldItem> opt = super.tryRemove(uid);
        opt.ifPresent(item -> playerItems.remove(playerId, item));
        return opt;
    }

    @Override
    public WorldItemWallet createTemp() {
        return WorldItemWallets.createTemporary(getItemStream(), kamas.get());
    }
}
