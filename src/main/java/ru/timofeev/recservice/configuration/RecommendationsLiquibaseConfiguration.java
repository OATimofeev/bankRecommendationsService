package ru.timofeev.recservice.configuration;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class RecommendationsLiquibaseConfiguration {

    @Bean
    public SpringLiquibase recommendationsLiquibase(
            @Qualifier("recommendationsDataSource") DataSource recommendationsDataSource
    ) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(recommendationsDataSource);
        liquibase.setChangeLog("classpath:liquibase/changelog-master.yml");
        liquibase.setShouldRun(true);
        return liquibase;
    }
}