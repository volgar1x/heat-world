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
public class SetDetailsReq implements Serializable {
    public final String publicAddress;
    public final int publicPort;
    public final byte completion;
    public final double date;
}
