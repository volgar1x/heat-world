package org.heat.login.users;

import org.fungsi.concurrent.Future;
import org.heat.User;

public interface UserCredentialsStrategy {
    String getNewSalt();
    byte[] getPublicKey();
    Future<String> convert(User user, byte[] credentials);
}
