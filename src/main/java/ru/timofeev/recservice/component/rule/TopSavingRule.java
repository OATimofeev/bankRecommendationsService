package ru.timofeev.recservice.component.rule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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
                transactionsRepository.hasProductType(userId, "DEBIT")
                        && (transactionsRepository.getAmountForProduct(userId, "DEBIT", "DEPOSIT") >= 50000
                        || transactionsRepository.getAmountForProduct(userId, "SAVING", "DEPOSIT") >= 50000)
                        && (transactionsRepository.getAmountForProduct(userId, "DEBIT", "DEPOSIT") > transactionsRepository.getAmountForProduct(userId, "DEBIT", "WITHDRAW"));

        return checks ? Optional.of("TopSaving") : Optional.empty();
    }
}
