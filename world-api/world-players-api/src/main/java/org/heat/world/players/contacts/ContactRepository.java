package org.heat.world.players.contacts;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;

public interface ContactRepository {
    enum Kind { FRIEND, IGNORED }

    Future<Unit> link(int fromId, int toId, Kind kind);
    Future<Unit> unlink(int fromId, int toId);

    Future<int[]> getLinksFrom(int fromId, Kind kind);
}
