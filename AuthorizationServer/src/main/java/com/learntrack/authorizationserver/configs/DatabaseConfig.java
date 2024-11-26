package com.learntrack.authorizationserver.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DatabaseConfig {

    @Value("classpath:oauth2-registered-client-schema.sql")
    private Resource registeredClientSchema;

    @Value("classpath:oauth2-authorization-schema.sql")
    private Resource authorizationSchema;

    @Value("classpath:oauth2-authorization-consent-schema.sql")
    private Resource authorizationConsentSchema;

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(registeredClientSchema);
        populator.addScript(authorizationSchema);
        populator.addScript(authorizationConsentSchema);
        populator.setSeparator(";");
        return populator;
    }
}
