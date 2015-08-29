package org.heat.backend.messages;

import com.ankamagames.dofus.network.enums.ServerStatusEnum;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;

import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Wither
public class SetStatusReq implements Serializable {
    public final ServerStatusEnum newStatus;
}
