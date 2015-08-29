package org.heat.world.items;

import com.ankamagames.dofus.network.enums.CharacterInventoryPositionEnum;
import org.fungsi.Either;
import org.heat.shared.Pair;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class DelegateItemBag implements WorldItemBag {
    protected abstract WorldItemBag delegate();

    @Override
    public Optional<WorldItem> findByUid(int uid) {
        return delegate().findByUid(uid);
    }

    @Override
    public Stream<WorldItem> findByGid(int gid) {
        return delegate().findByGid(gid);
    }

    @Override
    public Stream<WorldItem> findByPosition(CharacterInventoryPositionEnum position) {
        return delegate().findByPosition(position);
    }

    @Override
    public Stream<WorldItem> findByNotPosition(CharacterInventoryPositionEnum position) {
        return delegate().findByNotPosition(position);
    }

    @Override
    public Stream<WorldItem> findEquiped() {
        return delegate().findEquiped();
    }

    @Override
    public Stream<WorldItem> findNonEquiped() {
        return delegate().findNonEquiped();
    }

    @Override
    public Stream<WorldItem> getItemStream() {
        return delegate().getItemStream();
    }

    @Override
    public void add(WorldItem item) {
        delegate().add(item);
    }

    @Override
    public void addAll(List<WorldItem> items) {
        delegate().addAll(items);
    }

    @Override
    public void update(WorldItem item) {
        delegate().update(item);
    }

    @Override
    public void remove(WorldItem item) {
        delegate().remove(item);
    }

    @Override
    public Optional<WorldItem> tryRemove(int uid) {
        return delegate().tryRemove(uid);
    }

    @Override
    public Either<Pair<WorldItem, WorldItem>, WorldItem> fork(WorldItem item, int quantity) {
        return delegate().fork(item, quantity);
    }

    @Override
    public Either<WorldItem, WorldItem> mergeOn(WorldItem item, CharacterInventoryPositionEnum position) {
        return delegate().mergeOn(item, position);
    }

    @Override
    public Either<WorldItem, WorldItem> merge(WorldItem item) {
        return delegate().merge(item);
    }

    @Override
    public Either<WorldItem, WorldItem> mergeOrMove(WorldItem item, CharacterInventoryPositionEnum position) {
        return delegate().mergeOrMove(item, position);
    }
}
