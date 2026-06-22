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
public class Invest500Rule implements RecommendationRule {

    private final TransactionsRepository transactionsRepository;

    @Override
    public Optional<Long> apply(UUID userId) {
        boolean checks =
                transactionsRepository.hasProductType(userId, ProductTypeEnum.DEBIT)
                        && !transactionsRepository.hasProductType(userId, ProductTypeEnum.INVEST)
                        && transactionsRepository.getAmountForProduct(userId, ProductTypeEnum.SAVING, TransactionTypeEnum.DEPOSIT) > 1000;

        return checks ? Optional.of(1L) : Optional.empty();
    }
}
