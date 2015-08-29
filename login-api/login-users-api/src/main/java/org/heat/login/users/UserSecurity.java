package org.heat.login.users;

import org.fungsi.concurrent.Future;
import org.heat.User;

public interface UserSecurity {
    String getNewSalt();
    byte[] getPublicKey();
    byte[] getAuthenticator();

    Future<User> authenticate(String username, byte[] credentials);
}
