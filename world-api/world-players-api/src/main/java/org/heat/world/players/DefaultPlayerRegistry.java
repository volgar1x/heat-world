package org.heat.world.players;

import com.github.blackrush.acara.EventBus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.heat.world.players.events.OfflinePlayerEvent;
import org.heat.world.players.events.OnlinePlayerEvent;
import org.rocket.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

@RequiredArgsConstructor
public final class DefaultPlayerRegistry implements PlayerRegistry {
    private final Map<Integer, Player> byId = new HashMap<>();
    private final Map<String, Player> byName = new HashMap<>();
    private final Map<Integer, Player> byUserId = new HashMap<>();
    private final StampedLock lock = new StampedLock();

    @Getter final EventBus eventBus;

    @Override
    public @Nullable Player findPlayer(int id) {
        long stamp = lock.readLock();
        try {
            return byId.get(id);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public @Nullable Player findPlayerByUserId(int userId) {
        long stamp = lock.readLock();
        try {
            return byUserId.get(userId);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public boolean isUserRegisted(int userId) {
        long stamp = lock.readLock();
        try {
            return byUserId.containsKey(userId);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public @Nullable Player findPlayerByName(String name) {
        long stamp = lock.readLock();
        try {
            return byName.get(name);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public void add(Player player) {
        long stamp = lock.readLock();
        try {
            // read
            if (byId.containsKey(player.getId())) {
                // now we are sure there won't be duplicates
                throw new PlayerAlreadyRegisteredException(player + " is already registered");
            }
            if (byUserId.containsKey(player.getUserId())) {
                throw new UserAlreadyRegisteredException(player.getUserId() + " has already a used connected");
            }

            // write
            stamp = lock.tryConvertToWriteLock(stamp);
            if (stamp == 0L) {
                stamp = lock.writeLock();
            }

            byId.put(player.getId(), player);
            byName.put(player.getName(), player);
            byUserId.put(player.getUserId(), player);
        } finally {
            lock.unlock(stamp); // can unlock both read and write
        }

        eventBus.publish(new OnlinePlayerEvent(player));
    }

    @Override
    public void remove(Player player) {
        long stamp = lock.readLock();
        try {
            // read
            if (!byId.containsKey(player.getId())) {
                // avoid costly remove operation
                return;
            }

            // write
            stamp = lock.tryConvertToWriteLock(stamp);
            if (stamp == 0L) {
                stamp = lock.writeLock();
            }

            byId.remove(player.getId());
            byName.remove(player.getName());
            byUserId.remove(player.getUserId());
        } finally {
            lock.unlock(stamp); // can unlock both read and write
        }

        eventBus.publish(new OfflinePlayerEvent(player));
    }
}
