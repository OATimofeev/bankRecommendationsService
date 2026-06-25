package ru.timofeev.recservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.timofeev.recservice.dto.recommendations.GetRecommendationResponseDto;
import ru.timofeev.recservice.service.RecommendationProductsService;

import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
@AllArgsConstructor
public class RecommendationController {

    private final RecommendationProductsService recommendationProductsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все рекомендации для пользователя")
    @ApiResponse(responseCode = "200", description = "Список получен")
    public ResponseEntity<GetRecommendationResponseDto> get(@RequestParam UUID userId) {
        return ResponseEntity.ok(recommendationProductsService.getRecommendations(userId));
    }
}
