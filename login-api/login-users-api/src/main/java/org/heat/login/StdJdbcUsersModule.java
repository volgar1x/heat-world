package org.heat.login;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import org.fungsi.concurrent.Worker;
import org.fungsi.concurrent.Workers;
import org.heat.User;
import org.heat.login.users.JdbcUserRepository;
import org.heat.login.users.UserRepository;
import org.heat.login.users.UserTable;
import org.heat.shared.database.Table;

import java.util.concurrent.ExecutorService;

public class StdJdbcUsersModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(UserRepository.class).to(JdbcUserRepository.class).asEagerSingleton();
        expose(UserRepository.class);
    }

    @Provides
    Table<User> provideUserTable() {
        return new UserTable();
    }

    @Provides
    Worker provideWorker(ExecutorService executor) {
        return Workers.wrap(executor);
    }
}
