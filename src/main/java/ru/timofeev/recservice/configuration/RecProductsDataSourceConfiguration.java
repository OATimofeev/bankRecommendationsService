package ru.timofeev.recservice.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RecProductsDataSourceConfiguration {

    @Bean(name = "recProductsDataSource")
    public DataSource recProductsDataSource(@Value("${application.rec-products-db.url}") String recommendationsUrl,
                                                @Value("${application.rec-products-db.driver-class-name}") String driver,
                                                @Value("${application.rec-products-db.read-only}") boolean readOnly) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(recommendationsUrl);
        dataSource.setDriverClassName(driver);
        dataSource.setReadOnly(readOnly);
        return dataSource;
    }

    @Bean(name = "recProductsJdbcTemplate")
    public JdbcTemplate recProductsJdbcTemplate(@Qualifier("recProductsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
