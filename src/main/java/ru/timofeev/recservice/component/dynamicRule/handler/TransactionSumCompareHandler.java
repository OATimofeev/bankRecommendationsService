package ru.timofeev.recservice.component.dynamicRule.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.timofeev.recservice.component.dynamicRule.QueryType;
import ru.timofeev.recservice.model.enums.ProductTypeEnum;
import ru.timofeev.recservice.model.enums.TransactionTypeEnum;
import ru.timofeev.recservice.repository.TransactionsRepository;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionSumCompareHandler implements RuleConditionHandler {

    private final TransactionsRepository transactionsRepository;

    @Override
    public QueryType supportedQuery() {
        return QueryType.TRANSACTION_SUM_COMPARE;
    }

    @Override
    public boolean check(UUID userId, List<String> arguments) {
        RuleParseUtil.checkUserId(userId);
        RuleParseUtil.checkArgsSize(arguments, 4, QueryType.TRANSACTION_SUM_COMPARE.toString());
        ProductTypeEnum productType = RuleParseUtil.parseProductType(arguments.get(0));
        TransactionTypeEnum transactionType = RuleParseUtil.parseTransactionType(arguments.get(1));
        String operation = RuleParseUtil.parseComparisonOperation(arguments.get(2));
        Integer sum = RuleParseUtil.parseSum(arguments.get(3));

        return RuleParseUtil.compare(transactionsRepository.getAmountForProduct(userId, productType, transactionType), operation, sum);
    }
}
