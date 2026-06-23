package ru.timofeev.recservice.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.timofeev.recservice.model.enums.ProductTypeEnum;
import ru.timofeev.recservice.model.enums.TransactionTypeEnum;

import java.time.Duration;
import java.util.UUID;

@Repository
@Slf4j
public class TransactionsRepository {
    private final JdbcTemplate jdbcTemplate;

    private final Cache<UserOfKey, Boolean> hasProductTypeCache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .recordStats()
            .build();

    private final Cache<UserOfKey, Boolean> isActiveUserOfProductTypeCache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .recordStats()
            .build();

    private final Cache<AmountOfProductKey, Integer> amountForProductCache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .recordStats()
            .build();

    public TransactionsRepository(@Qualifier("transactionsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasProductType(UUID userId, ProductTypeEnum productType) {
        UserOfKey key = new UserOfKey(userId, productType);
        Boolean result = hasProductTypeCache.get(key, this::loadHasProductType);
        return Boolean.TRUE.equals(result);
    }

    public boolean isActiveUserOfProductType(UUID userId, ProductTypeEnum productType) {
        UserOfKey key = new UserOfKey(userId, productType);
        Boolean result = isActiveUserOfProductTypeCache.get(key, this::loadIsActiveUserOfProductType);
        return Boolean.TRUE.equals(result);
    }

    public int getAmountForProduct(UUID userId, ProductTypeEnum productType, TransactionTypeEnum transactionType) {
        AmountOfProductKey key = new AmountOfProductKey(userId, productType, transactionType);
        Integer result = amountForProductCache.get(key, this::loadGetAmountForProduct);
        return result != null ? result : 0;
    }

    private Boolean loadHasProductType(UserOfKey key) {
        log.info("Was invoked method loadHasProduct for user = {} and productType = {} in TransactionRepository", key.userId(), key.productType().name());
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
                key.userId(),
                key.productType().name()
        );
    }

    private Boolean loadIsActiveUserOfProductType(UserOfKey key) {
        log.info("Was invoked method loadIsActiveUserOfProductType for user = {} and productType = {} in TransactionRepository", key.userId(), key.productType().name());
        return jdbcTemplate.queryForObject("""
                        SELECT COUNT(T.ID) >= 5 FROM TRANSACTIONS T
                        JOIN PRODUCTS P ON T.PRODUCT_ID = P.ID
                        WHERE T.USER_ID = ?
                        AND P.TYPE = ?
                        """,
                Boolean.class,
                key.userId(),
                key.productType().name()
        );
    }

    private Integer loadGetAmountForProduct(AmountOfProductKey key) {
        log.info("Was invoked method loadGetAmountForProduct for user = {}, productType = {}, transactionType = {} in TransactionRepository",
                key.userId(), key.productType().name(), key.transactionType().name());
        return jdbcTemplate.queryForObject("""
                        SELECT SUM(t.AMOUNT)
                             FROM TRANSACTIONS t
                                      JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
                             WHERE t.USER_ID = ?
                                AND p.TYPE = ?
                                AND t.TYPE = ?
                        """,
                Integer.class,
                key.userId(),
                key.productType().name(),
                key.transactionType().name()
        );
    }

    public record UserOfKey(UUID userId, ProductTypeEnum productType) {
    }

    public record AmountOfProductKey(UUID userId, ProductTypeEnum productType, TransactionTypeEnum transactionType) {
    }

}
