package org.heat.backend.messages;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;
import org.heat.User;

import java.io.Serializable;

/**
 * @author Blackrush
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Wither
public class GetUserResp implements Serializable {
    public final User user;
}
