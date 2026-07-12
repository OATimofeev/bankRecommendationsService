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

@Service
@Slf4j
@AllArgsConstructor
public class TransactionDataService {
    private final TransactionsRepository transactionsRepository;
    private final List<RecommendationRule> rules;
    private final RuleSetEvaluator ruleSetEvaluator;

    public Optional<UserModel> getUserByUsername(String username) {
        log.info("Was invoked method for get user data  by username = {}", username);
        return transactionsRepository.getUserByUsername(username);
    }

    public void invalidateCache() {
        log.info("Was invoked method for clear transaction cache");
        transactionsRepository.invalidateAllCaches();
    }

    public List<Long> getMatchedStaticRules(UUID userId) {
        log.info("Try to get static recommendations for userId = {}", userId);
        return rules.stream()
                .map(rule -> rule.apply(userId))
                .flatMap(Optional::stream)
                .toList();
    }

    public List<RecommendationModel> evaluateDynamicRules(UUID userId, List<RecommendationModel> ruleSet) {
        log.info("Try to get dynamic recommendations for userId = {}", userId);
        return
                ruleSet
                        .stream()
                        .filter(x -> ruleSetEvaluator.evaluate(userId, x.getRuleSet()))
                        .toList();
    }
}