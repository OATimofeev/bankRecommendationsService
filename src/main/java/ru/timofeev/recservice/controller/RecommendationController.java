package ru.timofeev.recservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.timofeev.recservice.dto.GetRecommendationResponseDto;
import ru.timofeev.recservice.service.RecommendationProductsService;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
@AllArgsConstructor
public class RecommendationController {

    private final RecommendationProductsService recommendationProductsService;


    @GetMapping
    public ResponseEntity<GetRecommendationResponseDto> get(@RequestParam UUID userId) {
        return ResponseEntity.ok(recommendationProductsService.getRecommendations(userId));
    }
}
