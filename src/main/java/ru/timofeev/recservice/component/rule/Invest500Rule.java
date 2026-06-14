package ru.timofeev.recservice.component.rule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.timofeev.recservice.repository.TransactionsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class Invest500Rule implements RecommendationRule {

    private final TransactionsRepository transactionsRepository;

    @Override
    public Optional<String> apply(UUID userId) {
        boolean checks =
                transactionsRepository.hasProductType(userId, "DEBIT")
                        && !transactionsRepository.hasProductType(userId, "INVEST")
                        && transactionsRepository.getAmountForProduct(userId, "SAVING") > 1000;

        return checks ? Optional.of("Invest500") : Optional.empty();
    }
}
