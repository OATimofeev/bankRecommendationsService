package ru.timofeev.recservice.component.rule;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRule {
    Optional<String> apply(UUID userId);
}
