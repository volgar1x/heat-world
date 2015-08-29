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
public class IdentificationReq implements Serializable {
    public final int id;
}
