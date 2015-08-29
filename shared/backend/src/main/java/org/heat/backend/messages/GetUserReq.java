package org.heat.backend.messages;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;

import java.io.Serializable;

/**
 * @author Blackrush
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Wither
public class GetUserReq implements Serializable {
    public final int userId;
}
