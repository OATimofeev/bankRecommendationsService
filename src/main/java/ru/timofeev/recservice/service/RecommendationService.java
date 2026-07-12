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

@Service
@Slf4j
@AllArgsConstructor
public class RecommendationService {

    private final RecommendationsRepository recommendationsRepository;
    private final RecommendationMapper recommendationMapper;
    private final RuleStatService ruleStatService;
    private final TransactionDataService transactionDataService;

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

    public GetRecommendationResponseDto getRecommendationsResponseDto(UUID userId) {
        log.info("Was invoked method for prepare GetRecommendationResponseDto for userId = {}", userId);
        return GetRecommendationResponseDto.builder()
                .userId(userId)
                .recommendations(getRecommendations(userId))
                .build();
    }

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

    @Transactional
    public ProductDto create(ProductDto product) {
        log.info("Was invoked method for create new recommendation product for product id = {}", product.getProductId());
        product.setId(null);
        RecommendationModel createdRule = recommendationsRepository.save(
                recommendationMapper.getModelFromProductDto(product));
        ruleStatService.create(createdRule);
        return recommendationMapper.getProductDtoFromModel(createdRule);
    }

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