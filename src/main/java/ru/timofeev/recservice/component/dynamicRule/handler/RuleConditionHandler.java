package ru.timofeev.recservice.component.dynamicRule.handler;

import ru.timofeev.recservice.component.dynamicRule.QueryType;

import java.util.List;
import java.util.UUID;

public interface RuleConditionHandler {

    QueryType supportedQuery();

    boolean check(UUID userId, List<String> arguments);
}
