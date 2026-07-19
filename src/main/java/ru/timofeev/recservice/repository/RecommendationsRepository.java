package ru.timofeev.recservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.enums.RecommendationRuleType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с рекомендациями банковских продуктов.
 * <p>
 * Предоставляет методы поиска рекомендаций по типу правила
 * и по идентификатору продукта.
 */
public interface RecommendationsRepository extends JpaRepository<RecommendationModel, Long> {

    /**
     * Возвращает все рекомендации указанного типа.
     *
     * @param ruleType тип правила рекомендации
     * @return список рекомендаций с указанным типом правила
     */
    List<RecommendationModel> findAllByRuleType(RecommendationRuleType ruleType);

    /**
     * Возвращает рекомендацию по идентификатору продукта и типу правила.
     *
     * @param productId идентификатор продукта
     * @param ruleType  тип правила рекомендации
     * @return найденная рекомендация или пустой Optional
     */
    Optional<RecommendationModel> findByProductIdAndRuleType(
            UUID productId,
            RecommendationRuleType ruleType
    );
}