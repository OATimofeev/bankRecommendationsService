package ru.timofeev.recservice.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TransactionsRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionsRepository(@Qualifier("transactionsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getRandomTransactionAmount(UUID userId) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM transactions t WHERE t.user = ? LIMIT 1",
                Integer.class,
                userId);
        return Optional.ofNullable(result).orElse(0);
    }

    public Boolean hasProductType(UUID userId, String productType) {
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

    public Integer getAmountForProduct(UUID userId, String productType) {
        return jdbcTemplate.queryForObject("""
                        SELECT SUM(t.AMOUNT)
                             FROM TRANSACTIONS t
                                      JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
                             WHERE t.USER_ID = ?
                                AND p.TYPE = ?
                        """,
                Integer.class,
                userId,
                productType
        );
    }
}
