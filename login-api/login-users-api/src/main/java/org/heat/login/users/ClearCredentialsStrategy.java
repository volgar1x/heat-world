package org.heat.login.users;

import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.User;
import org.heat.shared.Strings;

import java.util.Random;

public final class ClearCredentialsStrategy implements UserCredentialsStrategy {

    public static final byte[] EMPTY_BYTES = new byte[0];

    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public byte[] getPublicKey() {
        return EMPTY_BYTES;
    }

    @Override
    public String getNewSalt() {
        return Strings.randomString(random, 32);
    }

    @Override
    public Future<String> convert(User user, byte[] credentials) {
        return Futures.success(new String(credentials));
    }
}
