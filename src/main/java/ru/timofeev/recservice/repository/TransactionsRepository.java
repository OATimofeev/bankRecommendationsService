package ru.timofeev.recservice.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.timofeev.recservice.model.enums.ProductTypeEnum;
import ru.timofeev.recservice.model.enums.TransactionTypeEnum;

import java.util.UUID;

@Repository
public class TransactionsRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionsRepository(@Qualifier("transactionsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean hasProductType(UUID userId, ProductTypeEnum productType) {
        return jdbcTemplate.queryForObject("""
                        SELECT EXISTS (
                            SELECT 1
                            FROM TRANSACTIONS t
                                     JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
                            WHERE t.USER_ID = ?
                              AND p.TYPE = ?
                        )
                        """,
                Boolean.class,
                userId,
                productType
        );
    }

    public Integer getAmountForProduct(UUID userId, ProductTypeEnum productType, TransactionTypeEnum transactionType) {
        return jdbcTemplate.queryForObject("""
                        SELECT SUM(t.AMOUNT)
                             FROM TRANSACTIONS t
                                      JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
                             WHERE t.USER_ID = ?
                                AND p.TYPE = ?
                                AND t.TYPE = ?
                        """,
                Integer.class,
                userId,
                productType,
                transactionType
        );
    }
}
