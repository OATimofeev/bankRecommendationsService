package ru.timofeev.recservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.component.mapper.RecommendationMapper;
import ru.timofeev.recservice.component.rule.Invest500Rule;
import ru.timofeev.recservice.dto.GetRecommendationResponseDto;
import ru.timofeev.recservice.repository.RecommendationsRepository;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class RecommendationProductsService {

    private final Invest500Rule invest500Rule;
    private final RecommendationsRepository recommendationsRepository;
    private final RecommendationMapper recommendationMapper;

    public GetRecommendationResponseDto getRecommendations(UUID userId) {
        log.info("Was invoked method for get recommendations for userId = {}", userId);
        GetRecommendationResponseDto responseDto =
                GetRecommendationResponseDto
                        .builder()
                        .userId(userId)
                        .build();
        invest500Rule.apply(userId)
                .map(recommendationsRepository::getRecommendationByRuleCode)
                .map(recommendationMapper::getDtoFromModel)
                .ifPresent(responseDto.getRecommendations()::add);
        return responseDto;
    }
}