package ru.timofeev.recservice.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.timofeev.recservice.component.dynamicRule.QueryType;
import ru.timofeev.recservice.component.dynamicRule.RuleConditionDto;
import ru.timofeev.recservice.component.dynamicRule.RuleSetEvaluator;
import ru.timofeev.recservice.component.dynamicRule.handler.RuleConditionHandler;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleSetEvaluatorTest {

    @Mock
    private RuleConditionHandler userOfHandler;

    @Mock
    private RuleConditionHandler activeUserOfHandler;

    private RuleSetEvaluator ruleSetEvaluator;

    @BeforeEach
    void setUp() {
        ruleSetEvaluator = new RuleSetEvaluator(List.of(userOfHandler, activeUserOfHandler));
    }

    @Test
    void evaluate_shouldReturnTrueWhenAllConditionsMatched() {
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

        when(userOfHandler.supportedQuery()).thenReturn(QueryType.USER_OF);
        when(activeUserOfHandler.supportedQuery()).thenReturn(QueryType.ACTIVE_USER_OF);

        when(userOfHandler.check(userId, firstCondition.getArguments())).thenReturn(true);
        when(activeUserOfHandler.check(userId, secondCondition.getArguments())).thenReturn(true);

        boolean result = ruleSetEvaluator.evaluate(userId, List.of(firstCondition, secondCondition));

        assertThat(result).isTrue();
    }

    @Test
    void evaluate_shouldReturnFalseWhenAtLeastOneConditionNotMatched() {
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

        when(userOfHandler.supportedQuery()).thenReturn(QueryType.USER_OF);
        when(activeUserOfHandler.supportedQuery()).thenReturn(QueryType.ACTIVE_USER_OF);

        when(userOfHandler.check(userId, firstCondition.getArguments())).thenReturn(true);
        when(activeUserOfHandler.check(userId, secondCondition.getArguments())).thenReturn(false);

        boolean result = ruleSetEvaluator.evaluate(userId, List.of(firstCondition, secondCondition));

        assertThat(result).isFalse();
    }

    @Test
    void evaluate_shouldInvertResultWhenNegateIsTrue() {
        UUID userId = UUID.randomUUID();

        RuleConditionDto condition = RuleConditionDto.builder()
                .queryType(QueryType.USER_OF)
                .arguments(List.of("DEBIT"))
                .negate(true)
                .build();

        when(userOfHandler.supportedQuery()).thenReturn(QueryType.USER_OF);
        when(userOfHandler.check(userId, condition.getArguments())).thenReturn(false);

        boolean result = ruleSetEvaluator.evaluate(userId, List.of(condition));

        assertThat(result).isTrue();
    }

    @Test
    void evaluate_shouldThrowExceptionWhenHandlerNotFound() {
        UUID userId = UUID.randomUUID();

        RuleConditionDto condition = RuleConditionDto.builder()
                .queryType(QueryType.TRANSACTION_SUM_COMPARE)
                .arguments(List.of("1000", ">", "500"))
                .negate(false)
                .build();

        when(userOfHandler.supportedQuery()).thenReturn(QueryType.USER_OF);
        when(activeUserOfHandler.supportedQuery()).thenReturn(QueryType.ACTIVE_USER_OF);

        assertThatThrownBy(() -> ruleSetEvaluator.evaluate(userId, List.of(condition)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No handle found for query type: " + QueryType.TRANSACTION_SUM_COMPARE);
    }

    @Test
    void evaluate_shouldReturnTrueForEmptyRuleSet() {
        UUID userId = UUID.randomUUID();

        boolean result = ruleSetEvaluator.evaluate(userId, List.of());

        assertThat(result).isTrue();
    }
}