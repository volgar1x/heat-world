package org.heat.backend.messages;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;

import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Wither
public class AckUserAuthReq implements Serializable {
    public final int userId;
    public final boolean success;
}
