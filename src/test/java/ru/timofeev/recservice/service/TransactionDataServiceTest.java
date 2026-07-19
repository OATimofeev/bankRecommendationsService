package ru.timofeev.recservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.timofeev.recservice.component.dynamicRule.QueryType;
import ru.timofeev.recservice.component.dynamicRule.RuleConditionDto;
import ru.timofeev.recservice.component.dynamicRule.RuleSetEvaluator;
import ru.timofeev.recservice.component.rule.RecommendationRule;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.UserModel;
import ru.timofeev.recservice.repository.TransactionsRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionDataServiceTest {

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private RuleSetEvaluator ruleSetEvaluator;

    @Mock
    private RecommendationRule firstRule;

    @Mock
    private RecommendationRule secondRule;

    @InjectMocks
    private TransactionDataService transactionDataService;

    @Test
    void getUserByUsername_shouldReturnUserWhenFound() {
        String username = "test_user";
        UserModel user = UserModel.builder()
                .id(UUID.randomUUID())
                .firstName("Ivan")
                .lastName("Ivanov")
                .build();

        when(transactionsRepository.getUserByUsername(username))
                .thenReturn(Optional.of(user));

        Optional<UserModel> result = transactionDataService.getUserByUsername(username);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    void getUserByUsername_shouldReturnEmptyWhenUserNotFound() {
        String username = "unknown_user";

        when(transactionsRepository.getUserByUsername(username))
                .thenReturn(Optional.empty());

        Optional<UserModel> result = transactionDataService.getUserByUsername(username);

        assertThat(result).isEmpty();
    }

    @Test
    void invalidateCache_shouldDelegateToRepository() {
        transactionDataService.invalidateCache();

        verify(transactionsRepository).invalidateAllCaches();
    }

    @Test
    void getMatchedStaticRules_shouldReturnOnlyMatchedRuleIds() {
        UUID userId = UUID.randomUUID();

        TransactionDataService service = new TransactionDataService(
                transactionsRepository,
                List.of(firstRule, secondRule),
                ruleSetEvaluator
        );

        when(firstRule.apply(userId)).thenReturn(Optional.of(1L));
        when(secondRule.apply(userId)).thenReturn(Optional.empty());

        List<Long> result = service.getMatchedStaticRules(userId);

        assertThat(result)
                .hasSize(1)
                .containsExactly(1L);
    }

    @Test
    void getMatchedStaticRules_shouldReturnAllMatchedRuleIds() {
        UUID userId = UUID.randomUUID();

        TransactionDataService service = new TransactionDataService(
                transactionsRepository,
                List.of(firstRule, secondRule),
                ruleSetEvaluator
        );

        when(firstRule.apply(userId)).thenReturn(Optional.of(1L));
        when(secondRule.apply(userId)).thenReturn(Optional.of(2L));

        List<Long> result = service.getMatchedStaticRules(userId);

        assertThat(result)
                .hasSize(2)
                .containsExactly(1L, 2L);
    }

    @Test
    void evaluateDynamicRules_shouldReturnOnlyMatchedRecommendations() {
        UUID userId = UUID.randomUUID();

        RuleConditionDto firstCondition = RuleConditionDto.builder()
                .queryType(QueryType.USER_OF)
                .arguments(List.of("DEBIT"))
                .negate(false)
                .build();

        RuleConditionDto secondCondition = RuleConditionDto.builder()
                .queryType(QueryType.ACTIVE_USER_OF)
                .arguments(List.of("CREDIT"))
                .negate(false)
                .build();

        List<RuleConditionDto> firstRuleSet = List.of(firstCondition);
        List<RuleConditionDto> secondRuleSet = List.of(secondCondition);

        RecommendationModel firstModel = RecommendationModel.builder()
                .id(1L)
                .ruleSet(firstRuleSet)
                .build();

        RecommendationModel secondModel = RecommendationModel.builder()
                .id(2L)
                .ruleSet(secondRuleSet)
                .build();

        when(ruleSetEvaluator.evaluate(userId, firstRuleSet)).thenReturn(true);
        when(ruleSetEvaluator.evaluate(userId, secondRuleSet)).thenReturn(false);

        List<RecommendationModel> result = transactionDataService.evaluateDynamicRules(
                userId,
                List.of(firstModel, secondModel)
        );

        assertThat(result)
                .hasSize(1)
                .containsExactly(firstModel);
    }

    @Test
    void evaluateDynamicRules_shouldReturnEmptyListWhenNoRulesMatched() {
        UUID userId = UUID.randomUUID();

        RuleConditionDto firstCondition = RuleConditionDto.builder()
                .queryType(QueryType.USER_OF)
                .arguments(List.of("DEBIT"))
                .negate(false)
                .build();

        RuleConditionDto secondCondition = RuleConditionDto.builder()
                .queryType(QueryType.ACTIVE_USER_OF)
                .arguments(List.of("CREDIT"))
                .negate(false)
                .build();

        List<RuleConditionDto> firstRuleSet = List.of(firstCondition);
        List<RuleConditionDto> secondRuleSet = List.of(secondCondition);

        RecommendationModel firstModel = RecommendationModel.builder()
                .id(1L)
                .ruleSet(firstRuleSet)
                .build();

        RecommendationModel secondModel = RecommendationModel.builder()
                .id(2L)
                .ruleSet(secondRuleSet)
                .build();

        when(ruleSetEvaluator.evaluate(userId, firstRuleSet)).thenReturn(false);
        when(ruleSetEvaluator.evaluate(userId, secondRuleSet)).thenReturn(false);

        List<RecommendationModel> result = transactionDataService.evaluateDynamicRules(
                userId,
                List.of(firstModel, secondModel)
        );

        assertThat(result).isEmpty();
    }
}