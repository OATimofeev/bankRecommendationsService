package ru.timofeev.recservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.component.dynamicRule.RuleSetEvaluator;
import ru.timofeev.recservice.component.mapper.RecommendationMapper;
import ru.timofeev.recservice.component.rule.RecommendationRule;
import ru.timofeev.recservice.dto.recommendations.GetRecommendationResponseDto;
import ru.timofeev.recservice.dto.recommendations.RecommendationDto;
import ru.timofeev.recservice.model.enums.RecommendationRuleType;
import ru.timofeev.recservice.repository.RecommendationsRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class RecommendationProductsService {

    private final List<RecommendationRule> rules;
    private final RecommendationsRepository recommendationsRepository;
    private final RecommendationMapper recommendationMapper;
    private final RuleSetEvaluator ruleSetEvaluator;

    public GetRecommendationResponseDto getRecommendations(UUID userId) {
        log.info("Was invoked method for get recommendations for userId = {}", userId);

        log.info("Try to get static recommendations for userId = {}", userId);
        List<RecommendationDto> staticRecommendations = rules.stream()
                .map(rule -> rule.apply(userId))
                .flatMap(Optional::stream)
                .map(recommendationsRepository::findById)
                .flatMap(Optional::stream)
                .map(recommendationMapper::getRecommendationDtoFromModel)
                .toList();

        log.info("Try to get dynamic recommendations for userId = {}", userId);
        List<RecommendationDto> dynamicRecommendations =
                recommendationsRepository
                        .findAllByRuleType(RecommendationRuleType.DYNAMIC)
                        .stream()
                        .filter(x -> ruleSetEvaluator.evaluate(userId, x.getRuleSet()))
                        .map(recommendationMapper::getRecommendationDtoFromModel)
                        .toList();

        log.info("Merge all recommendations for userId = {}", userId);
        List<RecommendationDto> allRecommendations = Stream.concat(staticRecommendations.stream(), dynamicRecommendations.stream()).toList();

        return GetRecommendationResponseDto.builder()
                .userId(userId)
                .recommendations(allRecommendations)
                .build();
    }
}