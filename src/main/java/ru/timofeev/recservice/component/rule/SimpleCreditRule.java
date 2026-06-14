package ru.timofeev.recservice.component.rule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.timofeev.recservice.repository.TransactionsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SimpleCreditRule implements RecommendationRule {

    private final TransactionsRepository transactionsRepository;

    @Override
    public Optional<String> apply(UUID userId) {
        boolean checks =
                !transactionsRepository.hasProductType(userId, "CREDIT")
                        && (transactionsRepository.getAmountForProduct(userId, "DEBIT", "DEPOSIT") > transactionsRepository.getAmountForProduct(userId, "DEBIT", "WITHDRAW"))
                        && transactionsRepository.getAmountForProduct(userId, "DEBIT", "WITHDRAW") > 100000;

        return checks ? Optional.of("SimpleCredit") : Optional.empty();
    }
}
