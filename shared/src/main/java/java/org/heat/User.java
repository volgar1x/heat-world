package org.heat;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
import java.util.OptionalInt;

@Data
public class User implements Serializable {
    int id;
    String username;
    String nickname;
    String salt;
    String hashpass;
    byte communityId;
    String secretQuestion;
    String secretAnswer;
    UserRank rank;
    OptionalInt lastServerId = OptionalInt.empty();
    Optional<Instant> subscriptionEnd = Optional.empty();
    Optional<Instant> banEnd = Optional.empty();
    boolean connected = false;
    Instant createdAt; // auto-filled by db
    Instant updatedAt; // auto-filled by db
    OptionalInt currentWorldId = OptionalInt.empty();

    public double getSubscriptionEndMilliOrZero() {
        if (!subscriptionEnd.isPresent()) {
            return 0.0;
        }
        return subscriptionEnd.get().toEpochMilli();
    }
}
