package ru.timofeev.recservice.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.timofeev.recservice.model.UserModel;
import ru.timofeev.recservice.model.enums.ProductTypeEnum;
import ru.timofeev.recservice.model.enums.TransactionTypeEnum;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с транзакционными данными.
 * <p>
 * Содержит SQL‑запросы и кеширование для проверки наличия продуктов,
 * активности пользователей и агрегирования сумм транзакций.
 */
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

    private final Cache<UserKey, Optional<UserModel>> getUserIdCache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .recordStats()
            .build();

    public TransactionsRepository(@Qualifier("transactionsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Проверяет, есть ли у пользователя продукты указанного типа.
     */
    public boolean hasProductType(UUID userId, ProductTypeEnum productType) {
        UserOfKey key = new UserOfKey(userId, productType);
        Boolean result = hasProductTypeCache.get(key, this::loadHasProductType);
        return Boolean.TRUE.equals(result);
    }

    /**
     * Проверяет, является ли пользователь активным по продуктам указанного типа.
     * Критерий активности определён в SQL‑запросе (например, COUNT(T.ID) >= 5).
     */
    public boolean isActiveUserOfProductType(UUID userId, ProductTypeEnum productType) {
        UserOfKey key = new UserOfKey(userId, productType);
        Boolean result = isActiveUserOfProductTypeCache.get(key, this::loadIsActiveUserOfProductType);
        return Boolean.TRUE.equals(result);
    }

    /**
     * Возвращает суммарный объём операций пользователя по продуктам указанного типа
     * и типу транзакции.
     *
     * @return сумма или 0, если данных нет
     */
    public int getAmountForProduct(UUID userId,
                                   ProductTypeEnum productType,
                                   TransactionTypeEnum transactionType) {
        AmountOfProductKey key = new AmountOfProductKey(userId, productType, transactionType);
        Integer result = amountForProductCache.get(key, this::loadGetAmountForProduct);
        return result != null ? result : 0;
    }

    /**
     * Возвращает пользователя по username с использованием кеша.
     */
    public Optional<UserModel> getUserByUsername(String userName) {
        UserKey key = new UserKey(userName);
        return getUserIdCache.get(key, this::loadGetUserIdByUsername);
    }

    private Boolean loadHasProductType(UserOfKey key) {
        log.info(
                "Was invoked method loadHasProduct for user = {} and productType = {} in TransactionRepository",
                key.userId(), key.productType().name()
        );
        return jdbcTemplate.queryForObject(
                """
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
        log.info(
                "Was invoked method loadIsActiveUserOfProductType for user = {} and productType = {} in TransactionRepository",
                key.userId(), key.productType().name()
        );
        return jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(T.ID) >= 5
                        FROM TRANSACTIONS T
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
        log.info(
                "Was invoked method loadGetAmountForProduct for user = {}, productType = {}, transactionType = {} in TransactionRepository",
                key.userId(), key.productType().name(), key.transactionType().name()
        );
        return jdbcTemplate.queryForObject(
                """
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

    private Optional<UserModel> loadGetUserIdByUsername(UserKey key) {
        log.info(
                "Was invoked method loadGetUserIdByUsername for username = {} in TransactionRepository",
                key.username()
        );

        List<UserModel> users = jdbcTemplate.query(
                """
                        SELECT u.ID,
                               u.FIRST_NAME,
                               u.LAST_NAME
                        FROM USERS u
                        WHERE u.USERNAME = ?
                        """,
                (rs, rowNum) -> UserModel.builder()
                        .id(rs.getObject("ID", UUID.class))
                        .firstName(rs.getString("FIRST_NAME"))
                        .lastName(rs.getString("LAST_NAME"))
                        .build(),
                key.username()
        );

        if (users.isEmpty()) {
            log.warn("User with username {} not found in USERS", key.username());
            return Optional.empty();
        }

        if (users.size() > 1) {
            log.error("Found {} users with same username {} in USERS", users.size(), key.username());
            return Optional.empty();
        }

        return Optional.of(users.get(0));
    }

    /**
     * Очищает все внутренние кэши репозитория.
     */
    public void invalidateAllCaches() {
        hasProductTypeCache.invalidateAll();
        isActiveUserOfProductTypeCache.invalidateAll();
        amountForProductCache.invalidateAll();
        getUserIdCache.invalidateAll();
    }

    public record UserOfKey(UUID userId, ProductTypeEnum productType) {
    }

    public record AmountOfProductKey(UUID userId,
                                     ProductTypeEnum productType,
                                     TransactionTypeEnum transactionType) {
    }

    public record UserKey(String username) {
    }
}