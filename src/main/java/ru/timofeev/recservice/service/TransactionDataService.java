package ru.timofeev.recservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.component.dynamicRule.RuleSetEvaluator;
import ru.timofeev.recservice.component.rule.RecommendationRule;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.UserModel;
import ru.timofeev.recservice.repository.TransactionsRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с транзакционными данными пользователей.
 * <p>
 * Используется для получения данных пользователя, очистки кэша
 * транзакционного репозитория, а также для вычисления статических
 * и динамических правил рекомендаций.
 */
@Service
@Slf4j
@AllArgsConstructor
public class TransactionDataService {
    private final TransactionsRepository transactionsRepository;
    private final List<RecommendationRule> rules;
    private final RuleSetEvaluator ruleSetEvaluator;

    /**
     * Возвращает пользователя по его username.
     *
     * @param username логин пользователя
     * @return найденный пользователь или пустой Optional, если пользователь не найден
     */
    public Optional<UserModel> getUserByUsername(String username) {
        log.info("Was invoked method for get user data by username = {}", username);
        return transactionsRepository.getUserByUsername(username);
    }

    /**
     * Очищает внутренние кэши репозитория транзакций.
     */
    public void invalidateCache() {
        log.info("Was invoked method for clear transaction cache");
        transactionsRepository.invalidateAllCaches();
    }

    /**
     * Возвращает список идентификаторов статических рекомендаций,
     * подходящих указанному пользователю.
     *
     * @param userId идентификатор пользователя
     * @return список идентификаторов рекомендаций
     */
    public List<Long> getMatchedStaticRules(UUID userId) {
        log.info("Try to get static recommendations for userId = {}", userId);
        return rules.stream()
                .map(rule -> rule.apply(userId))
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Возвращает список динамических рекомендаций, подходящих пользователю.
     *
     * @param userId  идентификатор пользователя
     * @param ruleSet набор динамических правил для проверки
     * @return список динамических рекомендаций, удовлетворяющих условиям
     */
    public List<RecommendationModel> evaluateDynamicRules(UUID userId, List<RecommendationModel> ruleSet) {
        log.info("Try to get dynamic recommendations for userId = {}", userId);
        return
                ruleSet
                        .stream()
                        .filter(x -> ruleSetEvaluator.evaluate(userId, x.getRuleSet()))
                        .toList();
    }
}