package ru.timofeev.recservice.component.rule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.timofeev.recservice.model.enums.ProductTypeEnum;
import ru.timofeev.recservice.model.enums.TransactionTypeEnum;
import ru.timofeev.recservice.repository.TransactionsRepository;

import java.util.Optional;
import java.util.UUID;

@Deprecated
@Component
@AllArgsConstructor
public class SimpleCreditRule implements RecommendationRule {

    private final TransactionsRepository transactionsRepository;

    @Override
    public Optional<Long> apply(UUID userId) {
        boolean checks =
                !transactionsRepository.hasProductType(userId, ProductTypeEnum.CREDIT)
                        && (transactionsRepository.getAmountForProduct(userId, ProductTypeEnum.DEBIT, TransactionTypeEnum.DEPOSIT) > transactionsRepository.getAmountForProduct(userId, ProductTypeEnum.DEBIT, TransactionTypeEnum.WITHDRAW))
                        && transactionsRepository.getAmountForProduct(userId, ProductTypeEnum.DEBIT, TransactionTypeEnum.WITHDRAW) > 100000;

        return checks ? Optional.of(2L) : Optional.empty();
    }
}
