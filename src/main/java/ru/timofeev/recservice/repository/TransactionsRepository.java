package ru.timofeev.recservice.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.timofeev.recservice.model.enums.ProductTypeEnum;
import ru.timofeev.recservice.model.enums.TransactionTypeEnum;
import ru.timofeev.recservice.repository.records.DepositWithdrawSums;

import java.util.UUID;

@Repository
public class TransactionsRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionsRepository(@Qualifier("transactionsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasProductType(UUID userId, ProductTypeEnum productType) {
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
                productType.name()
        );
    }

    public boolean isActiveUserOfProductType(UUID userId, ProductTypeEnum productType) {
        return jdbcTemplate.queryForObject("""
                        SELECT COUNT(T.ID) >= 5 FROM TRANSACTIONS T
                        JOIN PRODUCTS P ON T.PRODUCT_ID = P.ID
                        WHERE T.USER_ID = ?
                        AND P.TYPE = ?
                        """,
                Boolean.class,
                userId,
                productType.name()
        );
    }

    public DepositWithdrawSums getDepositWithdrawSums(UUID userId, ProductTypeEnum productType) {
        return jdbcTemplate.queryForObject("""
                        SELECT
                            COALESCE(SUM(CASE WHEN T.TYPE = 'DEPOSIT' THEN T.AMOUNT ELSE 0 END), 0) AS deposit_sum,
                            COALESCE(SUM(CASE WHEN T.TYPE = 'WITHDRAW' THEN T.AMOUNT ELSE 0 END), 0) AS withdraw_sum
                        FROM TRANSACTIONS T
                        JOIN PRODUCTS P ON T.PRODUCT_ID = P.ID
                        WHERE T.USER_ID = ?
                          AND P.TYPE = ?
                        """,
                (rs, rowNum) -> new DepositWithdrawSums(
                        rs.getInt("deposit_sum"),
                        rs.getInt("withdraw_sum")
                ),
                userId,
                productType.name()
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
                productType.name(),
                transactionType.name()
        );
    }

}
