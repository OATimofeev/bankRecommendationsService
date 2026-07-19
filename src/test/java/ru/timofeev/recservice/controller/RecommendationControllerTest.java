package ru.timofeev.recservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.timofeev.recservice.dto.recommendations.GetRecommendationResponseDto;
import ru.timofeev.recservice.service.RecommendationService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    void getRecommendations_shouldReturnOkResponseWithRecommendations() {
        UUID userId = UUID.randomUUID();

        GetRecommendationResponseDto responseDto = GetRecommendationResponseDto.builder()
                .userId(userId)
                .build();

        when(recommendationService.getRecommendationsResponseDto(userId)).thenReturn(responseDto);

        ResponseEntity<GetRecommendationResponseDto> response =
                recommendationController.get(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDto);

        verify(recommendationService).getRecommendationsResponseDto(userId);
        verifyNoMoreInteractions(recommendationService);
    }
}