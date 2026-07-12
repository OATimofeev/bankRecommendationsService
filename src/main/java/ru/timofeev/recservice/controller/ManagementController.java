package ru.timofeev.recservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.timofeev.recservice.service.RecommendationService;
import ru.timofeev.recservice.service.RuleStatService;
import ru.timofeev.recservice.service.TransactionDataService;

import java.net.HttpURLConnection;

@RestController
@RequestMapping("/management")
@AllArgsConstructor
public class ManagementController {

    private final RecommendationService recommendationService;
    private final RuleStatService ruleStatService;
    private final TransactionDataService transactionDataService;

    @PostMapping("/clear-caches")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Сбросить кэш БД транзакций")
    @ApiResponse(responseCode = "204", description = "Кэш успешно очищен")
    public ResponseEntity post() {
        transactionDataService.invalidateCache();
        return ResponseEntity.status(HttpURLConnection.HTTP_NO_CONTENT).build();
    }
}
