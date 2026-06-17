package ru.timofeev.recservice.component.rule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.timofeev.recservice.model.enums.ProductTypeEnum;
import ru.timofeev.recservice.model.enums.TransactionTypeEnum;
import ru.timofeev.recservice.repository.TransactionsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TopSavingRule implements RecommendationRule {

    private final TransactionsRepository transactionsRepository;

    @Override
    public Optional<String> apply(UUID userId) {
        boolean checks =
                transactionsRepository.hasProductType(userId, ProductTypeEnum.DEBIT)
                        && (transactionsRepository.getAmountForProduct(userId, ProductTypeEnum.DEBIT, TransactionTypeEnum.DEPOSIT) >= 50000
                        || transactionsRepository.getAmountForProduct(userId, ProductTypeEnum.SAVING, TransactionTypeEnum.DEPOSIT) >= 50000)
                        && (transactionsRepository.getAmountForProduct(userId, ProductTypeEnum.DEBIT, TransactionTypeEnum.DEPOSIT) > transactionsRepository.getAmountForProduct(userId, ProductTypeEnum.DEBIT, TransactionTypeEnum.WITHDRAW));

        return checks ? Optional.of("TopSaving") : Optional.empty();
    }
}
