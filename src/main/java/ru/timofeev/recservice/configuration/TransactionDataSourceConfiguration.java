package ru.timofeev.recservice.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class TransactionDataSourceConfiguration {

    @Primary
    @Bean(name = "transactionsDataSource")
    public DataSource transactionsDataSource(@Value("${application.transactions-db.url}") String transactionsUrl,
                                             @Value("${application.transactions-db.driver-class-name}") String driver,
                                             @Value("${application.transactions-db.read-only}") boolean readOnly) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(transactionsUrl);
        dataSource.setDriverClassName(driver);
        dataSource.setReadOnly(readOnly);
        return dataSource;
    }

    @Bean(name = "transactionsJdbcTemplate")
    public JdbcTemplate transactionsJdbcTemplate(@Qualifier("transactionsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
