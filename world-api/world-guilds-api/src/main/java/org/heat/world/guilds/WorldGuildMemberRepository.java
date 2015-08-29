package org.heat.world.guilds;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;

import java.util.List;

public interface WorldGuildMemberRepository {
    Future<List<? extends WorldGuildMember>> findByGuild(WorldGuild guild);
    Future<Integer> findGuildOfMemberById(int memberId);

    Future<Unit> save(WorldGuildMember member);
    Future<Unit> remove(WorldGuildMember member);
}
