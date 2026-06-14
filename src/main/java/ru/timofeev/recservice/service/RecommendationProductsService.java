package ru.timofeev.recservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.dto.GetRecommendationResponseDto;
import ru.timofeev.recservice.repository.RecommendationsRepository;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class RecommendationProductsService {

    private final RecommendationsRepository recommendationsRepository;

    public GetRecommendationResponseDto getRecommendations(UUID userId) {
        log.info("Was invoked method for get recommendations for userId = {}", userId);
        return new GetRecommendationResponseDto();
    }
}