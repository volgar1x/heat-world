package org.heat.world.guilds;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;

public interface WorldGuildRepository {
    Future<WorldGuild> find(int id);
    Future<WorldGuild> findByMemberId(int memberId);

    Future<Unit> save(WorldGuild guild);

    int getLastId();

    default Future<WorldGuild> findByMember(WorldGuildMember member) {
        return findByMemberId(member.getActorId());
    }
}
