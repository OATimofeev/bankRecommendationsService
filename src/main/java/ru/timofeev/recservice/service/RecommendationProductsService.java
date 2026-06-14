package ru.timofeev.recservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.component.mapper.RecommendationMapper;
import ru.timofeev.recservice.component.rule.RecommendationRule;
import ru.timofeev.recservice.dto.GetRecommendationResponseDto;
import ru.timofeev.recservice.repository.RecommendationsRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class RecommendationProductsService {


    private final List<RecommendationRule> rules;
    private final RecommendationsRepository recommendationsRepository;
    private final RecommendationMapper recommendationMapper;

    public GetRecommendationResponseDto getRecommendations(UUID userId) {
        log.info("Was invoked method for get recommendations for userId = {}", userId);

        List<GetRecommendationResponseDto.RecommendationDto> recommendations = rules.stream()
                .map(rule -> rule.apply(userId))
                .flatMap(Optional::stream)
                .map(recommendationsRepository::getRecommendationByRuleCode)
                .map(recommendationMapper::getDtoFromModel)
                .toList();
        return GetRecommendationResponseDto.builder()
                .userId(userId)
                .recommendations(recommendations)
                .build();
    }
}