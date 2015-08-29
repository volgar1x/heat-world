package org.heat.world.users;

import com.ankamagames.dofus.network.enums.PlayerStateEnum;
import com.ankamagames.dofus.network.types.game.friend.FriendInformations;
import com.ankamagames.dofus.network.types.game.friend.IgnoredInformations;
import com.github.blackrush.acara.EventBus;
import lombok.*;
import org.heat.UserRank;

import java.time.Instant;
import java.util.Optional;
import java.util.OptionalInt;

@Getter
@Setter
@EqualsAndHashCode
@ToString(of = {"id", "nickname"})
@RequiredArgsConstructor
public final class WorldUser {
    final EventBus eventBus;

    int id;
    String nickname;
    org.heat.User user;
    int channels;
    Instant lastConnection;
    boolean listeningFriends;

    public static final int CHANNELS_COUNT = 13;

    public byte[] getChannelsAsBytes() {
        byte[] result = new byte[Integer.bitCount(channels)];
        int i = 0, j = 1;
        byte id = 0;
        for (int k = 0; k < CHANNELS_COUNT; k++) {
            if ((channels & j) != 0) {
                result[i++] = id;
            }
            j <<= 1;
            id++;
        }
        return result;
    }

    public byte[] getDisabledChannelsAsBytes() {
        int len = CHANNELS_COUNT - Integer.bitCount(channels);
        if (len <= 0) {
            return new byte[0];
        }
        byte[] result = new byte[len];
        int i = 0, j = 1;
        byte id = 0;
        for (int k = 0; k < CHANNELS_COUNT; k++) {
            if ((channels & j) == 0) {
                result[i++] = id;
            }
            j <<= 1;
            id++;
        }
        return result;
    }

    public boolean hasChannel(int id) {
        return (channels & (1 << id)) != 0;
    }

    public FriendInformations toFriendInformations() {
        return new FriendInformations(id, nickname, PlayerStateEnum.NOT_CONNECTED.value,
                                      (int) lastConnection.getEpochSecond(),
                                      0/*achievement*/);
    }

    public IgnoredInformations toIgnoredInformations() {
        return new IgnoredInformations(id, nickname);
    }

    //<editor-fold desc="org.heat.User delegate">
    public double getSubscriptionEndMilliOrZero() {
        return user.getSubscriptionEndMilliOrZero();
    }

    public String getHashpass() {
        return user.getHashpass();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getSalt() {
        return user.getSalt();
    }

    public String getSecretQuestion() {
        return user.getSecretQuestion();
    }

    public boolean isConnected() {
        return user.isConnected();
    }

    public String getSecretAnswer() {
        return user.getSecretAnswer();
    }

    public Optional<Instant> getBanEnd() {
        return user.getBanEnd();
    }

    public UserRank getRank() {
        return user.getRank();
    }

    public Instant getCreatedAt() {
        return user.getCreatedAt();
    }

    public OptionalInt getCurrentWorldId() {
        return user.getCurrentWorldId();
    }

    public Instant getUpdatedAt() {
        return user.getUpdatedAt();
    }

    public Optional<Instant> getSubscriptionEnd() {
        return user.getSubscriptionEnd();
    }

    public OptionalInt getLastServerId() {
        return user.getLastServerId();
    }

    public byte getCommunityId() {
        return user.getCommunityId();
    }
    //</editor-fold>
}
