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
public class TransactionSumCompareDepositWithdrawHandler implements RuleConditionHandler {

    private final TransactionsRepository transactionsRepository;

    @Override
    public QueryType supportedQuery() {
        return QueryType.TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW;
    }

    @Override
    public boolean check(UUID userId, List<String> arguments) {
        RuleParseUtil.checkUserId(userId);
        RuleParseUtil.checkArgsSize(arguments, 2, QueryType.TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW.toString());
        ProductTypeEnum productType = RuleParseUtil.parseProductType(arguments.get(0));
        String operation = RuleParseUtil.parseComparisonOperation(arguments.get(1));

        return RuleParseUtil.compare(
                transactionsRepository.getAmountForProduct(userId, productType, TransactionTypeEnum.DEPOSIT),
                operation,
                transactionsRepository.getAmountForProduct(userId, productType, TransactionTypeEnum.WITHDRAW));
    }
}
