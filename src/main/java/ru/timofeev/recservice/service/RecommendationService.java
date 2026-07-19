package ru.timofeev.recservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.component.mapper.RecommendationMapper;
import ru.timofeev.recservice.dto.recommendations.GetRecommendationResponseDto;
import ru.timofeev.recservice.dto.recommendations.RecommendationDto;
import ru.timofeev.recservice.dto.rule.GetRuleResponseDto;
import ru.timofeev.recservice.dto.rule.ProductDto;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.enums.RecommendationRuleType;
import ru.timofeev.recservice.model.enums.StatNameEnum;
import ru.timofeev.recservice.repository.RecommendationsRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Сервис для работы с рекомендациями банковских продуктов.
 * <p>
 * Отвечает за получение рекомендаций по пользователю, управление
 * динамическими правилами рекомендаций и обновление статистики
 * срабатывания правил.
 */
@Service
@Slf4j
@AllArgsConstructor
public class RecommendationService {

    private final RecommendationsRepository recommendationsRepository;
    private final RecommendationMapper recommendationMapper;
    private final RuleStatService ruleStatService;
    private final TransactionDataService transactionDataService;

    /**
     * Возвращает список рекомендаций для указанного пользователя.
     * <p>
     * 1. Вычисляет статические рекомендации на основе правил первого ТЗ.<br>
     * 2. Получает динамические рекомендации на основе JSON‑правил.<br>
     * 3. Объединяет оба списка, обновляет статистику срабатываний и
     * маппит модели в DTO.
     *
     * @param userId идентификатор пользователя
     * @return список DTO с рекомендациями
     */
    public List<RecommendationDto> getRecommendations(UUID userId) {
        log.info("Was invoked method for get recommendations for userId = {}", userId);

        List<RecommendationModel> staticRecommendations =
                transactionDataService.getMatchedStaticRules(userId)
                        .stream()
                        .map(recommendationsRepository::findById)
                        .flatMap(Optional::stream)
                        .toList();

        List<RecommendationModel> dynamicRecommendations =
                transactionDataService.evaluateDynamicRules(userId, recommendationsRepository.findAllByRuleType(RecommendationRuleType.DYNAMIC));

        log.info("Merge all recommendations for userId = {}", userId);
        List<RecommendationModel> result =
                Stream.concat(staticRecommendations.stream(), dynamicRecommendations.stream()).toList();
        ruleStatService.incrementStat(result, StatNameEnum.TRIGGERED);

        return result
                .stream()
                .map(recommendationMapper::getRecommendationDtoFromModel)
                .toList();
    }

    /**
     * Возвращает DTO-обёртку с рекомендациями для пользователя.
     * <p>
     * Содержит идентификатор пользователя и список рекомендаций.
     *
     * @param userId идентификатор пользователя
     * @return DTO с рекомендациями и userId
     */
    public GetRecommendationResponseDto getRecommendationsResponseDto(UUID userId) {
        log.info("Was invoked method for prepare GetRecommendationResponseDto for userId = {}", userId);
        return GetRecommendationResponseDto.builder()
                .userId(userId)
                .recommendations(getRecommendations(userId))
                .build();
    }

    /**
     * Возвращает список всех динамических правил рекомендаций.
     *
     * @return DTO с данными по динамическим рекомендациям
     */
    public GetRuleResponseDto getAllDynamicRecs() {
        log.info("Was invoked method for get all DYNAMIC recommendations");
        return GetRuleResponseDto.builder()
                .data(recommendationsRepository
                        .findAllByRuleType(RecommendationRuleType.DYNAMIC)
                        .stream()
                        .map(recommendationMapper::getProductDtoFromModel)
                        .toList())
                .build();
    }

    /**
     * Создаёт новое динамическое правило рекомендации.
     * <p>
     * Очищает идентификатор в DTO, сохраняет правило, создаёт начальную
     * запись статистики и возвращает сохранённое правило в виде DTO.
     *
     * @param product DTO с данными правила
     * @return созданное правило в виде DTO
     */
    @Transactional
    public ProductDto create(ProductDto product) {
        log.info("Was invoked method for create new recommendation product for product id = {}", product.getProductId());
        product.setId(null);
        RecommendationModel createdRule = recommendationsRepository.save(
                recommendationMapper.getModelFromProductDto(product));
        ruleStatService.create(createdRule);
        return recommendationMapper.getProductDtoFromModel(createdRule);
    }

    /**
     * Удаляет динамическое правило рекомендации по идентификатору продукта.
     * <p>
     * При наличии правила сначала удаляет связанную статистику,
     * затем само правило.
     *
     * @param productId идентификатор продукта
     */
    @Transactional
    public void delete(UUID productId) {
        recommendationsRepository.findByProductIdAndRuleType(productId, RecommendationRuleType.DYNAMIC)
                .ifPresent(recommendation -> {
                    ruleStatService.delete(recommendation);

                    log.info("Was invoked method for delete recommendation product UUID : {}", productId);
                    recommendationsRepository.delete(recommendation);
                });
    }
}