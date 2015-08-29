package org.heat.login.users;

import com.ankamagames.dofus.network.enums.IdentificationFailureReasonEnum;
import com.ankamagames.dofus.network.types.version.Version;

import java.time.Instant;
import java.util.Optional;

public class UserAuthenticationException extends RuntimeException {
    private final IdentificationFailureReasonEnum reason;
    private final Instant banEnd;
    private final Version requiredVersion;

    public UserAuthenticationException(IdentificationFailureReasonEnum reason) {
        this.reason = reason;
        this.banEnd = null;
        this.requiredVersion = null;
    }

    public UserAuthenticationException(Instant banEnd) {
        this.reason = IdentificationFailureReasonEnum.BANNED;
        this.banEnd = banEnd;
        this.requiredVersion = null;
    }

    public UserAuthenticationException(Version requiredVersion) {
        this.reason = IdentificationFailureReasonEnum.BAD_VERSION;
        this.requiredVersion = requiredVersion;
        this.banEnd = null;
    }

    /**
     * Here you go sir, a free lightweight exception for your own good! Do not thank me ;-)
     * @return {@code this}
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public IdentificationFailureReasonEnum getReason() {
        return reason;
    }

    public Optional<Instant> getBanEnd() {
        return Optional.ofNullable(banEnd);
    }

    public double getBanEndMilliOrZero() {
        if (banEnd == null) {
            return 0.0;
        }
        return banEnd.toEpochMilli();
    }

    public Optional<Version> getRequiredVersion() {
        return Optional.ofNullable(requiredVersion);
    }
}
