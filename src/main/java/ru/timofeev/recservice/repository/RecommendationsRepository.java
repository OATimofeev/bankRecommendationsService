package ru.timofeev.recservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.enums.RecommendationRuleType;

import java.util.List;
import java.util.UUID;

public interface RecommendationsRepository extends JpaRepository<RecommendationModel, Long> {

    List<RecommendationModel> findAllByRuleType(RecommendationRuleType ruleType);

    void deleteByProductIdAndRuleType(UUID productId, RecommendationRuleType ruleType);
}
