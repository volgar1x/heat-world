package org.heat.world.players.shortcuts;

import org.fungsi.concurrent.Future;

import java.util.List;

public interface PlayerShortcutRepository {
    Future<List<PlayerShortcut>> findAll(int playerId);

    Future<PlayerShortcut> create(PlayerShortcut shortcut);
    Future<PlayerShortcut> remove(PlayerShortcut shortcut);
}
