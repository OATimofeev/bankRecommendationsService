package ru.timofeev.recservice.component.dynamicRule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.timofeev.recservice.component.dynamicRule.handler.RuleConditionHandler;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RuleSetEvaluator {

    private final List<RuleConditionHandler> handlers;

    public boolean evaluate(UUID userId, List<RuleConditionDto> ruleSet) {
        return ruleSet.stream()
                .allMatch(x -> evaluateCondition(userId, x));
    }

    private boolean evaluateCondition(UUID userId, RuleConditionDto ruleConditionDto) {
        RuleConditionHandler handler = handlers.stream()
                .filter(x -> x.supportedQuery() == ruleConditionDto.getQueryType())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No handle found for query type: " + ruleConditionDto.getQueryType()
                ));
        boolean res = handler.check(userId, ruleConditionDto.getArguments());
        return ruleConditionDto.isNegate() ? !res : res;
    }

}
