package ru.timofeev.recservice.component.rule;

import java.util.Optional;
import java.util.UUID;

@Deprecated
public interface RecommendationRule {
    Optional<Long> apply(UUID userId);
}
