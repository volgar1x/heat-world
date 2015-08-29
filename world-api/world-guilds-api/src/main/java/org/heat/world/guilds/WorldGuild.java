package org.heat.world.guilds;

import com.ankamagames.dofus.network.types.game.context.roleplay.BasicGuildInformations;
import com.ankamagames.dofus.network.types.game.context.roleplay.GuildInformations;
import com.ankamagames.dofus.network.types.game.guild.GuildEmblem;
import com.ankamagames.dofus.network.types.game.guild.GuildMember;
import com.github.blackrush.acara.EventBus;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Promise;
import org.fungsi.concurrent.Promises;
import org.heat.shared.LightweightException;
import org.heat.world.chat.VirtualWorldChannel;
import org.heat.world.roleplay.WorldAction;
import org.heat.world.roleplay.WorldActor;
import org.rocket.Nullable;

import java.util.stream.Stream;

public interface WorldGuild extends VirtualWorldChannel {
    EventBus getEventBus();

    int getId();
    String getName();
    GuildEmblem getEmblem();

    /**
     * Create an invitation that can be accepted or refused.
     * There is a limited amount of invitations available for one guild.
     * @param recruter the member of the guild that is recruting
     * @param recruted a yet to be member of this guild
     * @return an invitation to this guild, or {@code null} if there are no more invitations left
     */
    @Nullable Invitation invite(WorldGuildMember recruter, WorldGuildMember recruted);

    /**
     * Find a member of this guild.
     * @param id the member's ID
     * @return the found member, or {@code null} if the member ID wasnt valid
     */
    @Nullable WorldGuildMember findMember(int id);

    /**
     * Update a member of this guild.
     * @param updater                   the member updating a member
     * @param memberId                  the member's ID that will be updated
     * @param rank                      the member's new rank to set
     * @param givenExperiencePercent    the member's given experience percent to set
     * @param perms                     the member's permissions to set
     * @return a member that has been updated, or {@code null} if the member ID isnt valid or
     *         if no member has been updated
     */
    @Nullable WorldGuildMember update(WorldGuildMember updater, int memberId, short rank, byte givenExperiencePercent, WorldGuildPermissions perms);

    /**
     * Kick a member from this guild.
     * @param kicker    the member kicking a member of this guild
     * @param kickedId  the member's ID that will be kicked
     */
    void kick(WorldGuildMember kicker, int kickedId);

    /**
     * The maximum number of members that this guild can have.
     * @return a strictly positive integer
     */
    int getNrMembersLimit();

    BasicGuildInformations toBasicGuildInformations();
    GuildInformations toGuildInformations();
    Stream<GuildMember> toGuildMember();

    abstract class Invitation implements WorldAction {
        private final WorldGuildMember recruter, recruted;
        private final Promise<WorldAction> promise = Promises.create();

        protected Invitation(WorldGuildMember recruter, WorldGuildMember recruted) {
            this.recruter = recruter;
            this.recruted = recruted;
        }

        public abstract WorldGuild getGuild();
        public WorldGuildMember getRecruter() {return recruter;}
        public WorldGuildMember getRecruted() {return recruted;}
        public Future<WorldAction> getEndFuture() {return promise;}

        protected void onAccepted() {}
        protected void onRefused() {}

        public void answer(boolean accept) {
            if (accept) {
                onAccepted();
                promise.complete(this);
            } else {
                onRefused();
                promise.fail(new Failure());
            }
        }

        @Override
        public WorldActor getActor() {
            return getRecruted();
        }

        @Override
        public Future<WorldAction> cancel() {
            answer(false);
            return getEndFuture();
        }

        public static final class Failure extends LightweightException {
            public Failure() {
                super("Guild invitation has failed.");
            }
        }
    }

    class I18N {
        public static final int NO_MORE_MEMBERS = 538;
    }
}
