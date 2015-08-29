package org.heat.login;

import com.google.inject.PrivateModule;
import org.heat.login.users.DefaultUserSecurity;
import org.heat.login.users.RsaCredentialsStrategy;
import org.heat.login.users.UserCredentialsStrategy;
import org.heat.login.users.UserSecurity;

public class StdUsersModule extends PrivateModule {
    @Override
    protected void configure() {
//        bind(UserCredentialsStrategy.class).to(ClearCredentialsStrategy.class);
        bind(UserCredentialsStrategy.class).to(RsaCredentialsStrategy.class);
        bind(UserSecurity.class).to(DefaultUserSecurity.class).asEagerSingleton();

        expose(UserSecurity.class);
    }
}
