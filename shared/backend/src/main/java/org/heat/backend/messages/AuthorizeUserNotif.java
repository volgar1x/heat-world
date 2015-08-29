package org.heat.backend.messages;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;

import java.io.Serializable;
import java.time.Instant;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Wither
public class AuthorizeUserNotif implements Serializable {
    public final int userId;
    public final Instant updatedAt;
}
