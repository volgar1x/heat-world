package org.heat.login;

import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.heat.shared.Configs;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

public final class StdJdbcModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    /**
     * https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby
     */
    @Provides
    HikariConfig provideHikariConfig(Config c) {
        Config config = c.getConfig("heat.login.db");

        Properties props = new Properties();
        for (Map.Entry<String, ConfigValue> entry : config.getConfig("dataSource").entrySet()) {
            props.put("dataSource." + entry.getKey(), entry.getValue().unwrapped());
        }
        for (Map.Entry<String, ConfigValue> entry : config.withoutPath("dataSource").entrySet()) {
            props.put(entry.getKey(), entry.getValue().unwrapped());
        }

        return new HikariConfig(props);
    }

    @Provides
    CacheBuilder<Object, Object> provideCacheBuilder(Config config) {
        return Configs.newCacheBuilder(config);
    }

    @Provides
    @Singleton
    DataSource provideDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }
}
