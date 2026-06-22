package ru.timofeev.recservice.component.dynamicRule.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.timofeev.recservice.component.dynamicRule.QueryType;
import ru.timofeev.recservice.model.enums.ProductTypeEnum;
import ru.timofeev.recservice.repository.TransactionsRepository;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ActiveUserOfHandler implements RuleConditionHandler {

    private final TransactionsRepository transactionsRepository;

    @Override
    public QueryType supportedQuery() {
        return QueryType.ACTIVE_USER_OF;
    }

    @Override
    public boolean check(UUID userId, List<String> arguments) {
        RuleParseUtil.checkUserId(userId);
        RuleParseUtil.checkArgsSize(arguments, 1, QueryType.ACTIVE_USER_OF.toString());

        ProductTypeEnum productType = RuleParseUtil.parseProductType(arguments.getFirst());

        return transactionsRepository.isActiveUserOfProductType(userId, productType);
    }
}
