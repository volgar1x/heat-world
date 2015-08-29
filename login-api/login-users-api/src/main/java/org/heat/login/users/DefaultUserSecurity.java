package org.heat.login.users;

import lombok.SneakyThrows;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.User;
import org.heat.UserRank;
import org.heat.shared.Strings;

import javax.inject.Inject;
import java.security.MessageDigest;
import java.util.NoSuchElementException;

import static com.ankamagames.dofus.network.enums.IdentificationFailureReasonEnum.SPARE;
import static com.ankamagames.dofus.network.enums.IdentificationFailureReasonEnum.WRONG_CREDENTIALS;

public final class DefaultUserSecurity implements UserSecurity {
    private final UserRepository users;
    private final UserCredentialsStrategy credentialsStrategy;
    private final MessageDigest digest;

    @SneakyThrows
    @Inject
    public DefaultUserSecurity(UserRepository users, UserCredentialsStrategy credentialsStrategy) {
        this.users = users;
        this.credentialsStrategy = credentialsStrategy;
        this.digest = MessageDigest.getInstance("SHA-512");
    }

    @Override
    public byte[] getPublicKey() {
        return credentialsStrategy.getPublicKey();
    }

    @Override
    public String getNewSalt() {
        return credentialsStrategy.getNewSalt();
    }

    @Override
    public byte[] getAuthenticator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<User> authenticate(String username, byte[] credentials) {
        return findUser(username)
                .flatMap(user -> validatePassword(user, credentials))
                .<User>flatMap(this::assertNotBanned)
                .<User>flatMap(this::assertNotConnected)
                ;
    }

    private Future<User> findUser(String username) {
        return users.findByUsername(username)
                .mayRescue(err -> {
                    if (err instanceof NoSuchElementException) {
                        return Futures.failure(new UserAuthenticationException(WRONG_CREDENTIALS));
                    }
                    return Futures.failure(err);
                });
    }

    private Future<User> validatePassword(User user, byte[] credentials) {
        return credentialsStrategy.convert(user, credentials)
                .map(s -> hexdigest(user.getSalt(), s))
                .<User>flatMap(hash -> {
                    if (user.getHashpass().equals(hash)) {
                        return Futures.success(user);
                    }

                    return Futures.failure(new UserAuthenticationException(WRONG_CREDENTIALS));
                });
    }

    private Future<User> assertNotBanned(User user) {
        if (user.getRank().enough(UserRank.USER)) {
            return Futures.success(user);
        }

        return Futures.failure(new UserAuthenticationException(user.getBanEnd().orElse(null)));
    }

    private Future<User> assertNotConnected(User user) {
        if (user.isConnected() || user.getCurrentWorldId().isPresent()) {
            return Futures.failure(new UserAuthenticationException(SPARE));
        }

        return Futures.success(user);
    }

    private String hexdigest(String salt, String s) {
        return hexdigest(salt + hexdigest(s) + salt);
    }

    private String hexdigest(String s) {
        return Strings.toHexBytes(digest.digest(s.getBytes()));
    }
}
