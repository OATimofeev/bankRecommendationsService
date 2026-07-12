package ru.timofeev.recservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.component.dynamicRule.RuleSetEvaluator;
import ru.timofeev.recservice.component.mapper.RecommendationMapper;
import ru.timofeev.recservice.component.rule.RecommendationRule;
import ru.timofeev.recservice.dto.recommendations.GetRecommendationResponseDto;
import ru.timofeev.recservice.dto.recommendations.RecommendationDto;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.UserModel;
import ru.timofeev.recservice.model.enums.RecommendationRuleType;
import ru.timofeev.recservice.model.enums.StatNameEnum;
import ru.timofeev.recservice.repository.RecommendationsRepository;
import ru.timofeev.recservice.repository.TransactionsRepository;

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
    private final TransactionsRepository transactionsRepository;
    private final RecommendationMapper recommendationMapper;
    private final RuleSetEvaluator ruleSetEvaluator;

    private final RuleStatService ruleStatService;

    public List<RecommendationDto> getRecommendations(UUID userId) {
        log.info("Was invoked method for get recommendations for userId = {}", userId);

        log.info("Try to get static recommendations for userId = {}", userId);
        List<RecommendationModel> staticRecommendations = rules.stream()
                .map(rule -> rule.apply(userId))
                .flatMap(Optional::stream)
                .map(recommendationsRepository::findById)
                .flatMap(Optional::stream)
                .toList();

        log.info("Try to get dynamic recommendations for userId = {}", userId);
        List<RecommendationModel> dynamicRecommendations =
                recommendationsRepository
                        .findAllByRuleType(RecommendationRuleType.DYNAMIC)
                        .stream()
                        .filter(x -> ruleSetEvaluator.evaluate(userId, x.getRuleSet()))
                        .toList();

        log.info("Merge all recommendations for userId = {}", userId);
        List<RecommendationModel> result = Stream.concat(staticRecommendations.stream(), dynamicRecommendations.stream()).toList();
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

    public Optional<UserModel> getUserByUsername(String username) {
        log.info("Was invoked method for get user data  by username = {}", username);
        return transactionsRepository.getUserByUsername(username);
    }
}