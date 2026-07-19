package ru.timofeev.recservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.timofeev.recservice.dto.recommendations.GetRecommendationResponseDto;
import ru.timofeev.recservice.service.RecommendationService;

import java.util.UUID;

/**
 * REST-контроллер для получения персональных рекомендаций банковских продуктов.
 * <p>
 * Контроллер принимает идентификатор пользователя и возвращает список
 * рекомендаций, сформированный на основе набора бизнес-правил.
 */
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Возвращает список рекомендаций для указанного пользователя.
     * <p>
     * Формат эндпоинта соответствует техническому заданию:
     * {@code GET /recommendation/{user_id}}.
     *
     * @param userId идентификатор пользователя
     * @return объект ответа с идентификатором пользователя и списком рекомендаций
     */
    @GetMapping("/{user_id}")
    @Operation(summary = "Получить все рекомендации для пользователя")
    @ApiResponse(responseCode = "200", description = "Список рекомендаций успешно получен")
    public ResponseEntity<GetRecommendationResponseDto> get(
            @PathVariable("user_id") UUID userId
    ) {
        return ResponseEntity.ok(recommendationService.getRecommendationsResponseDto(userId));
    }
}