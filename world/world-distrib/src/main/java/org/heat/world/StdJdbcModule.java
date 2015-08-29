package org.heat.world;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

public class StdJdbcModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new StdJdbcPlayersModule());
    }

    /**
     * https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby
     */
    @Provides
    HikariConfig provideHikariConfig(Config c) {
        Config config = c.getConfig("heat.world.db");

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
    @Singleton
    DataSource provideDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }
}
