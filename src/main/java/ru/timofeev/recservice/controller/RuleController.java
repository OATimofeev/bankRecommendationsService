package ru.timofeev.recservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.timofeev.recservice.dto.rule.GetRuleResponseDto;
import ru.timofeev.recservice.dto.rule.ProductDto;
import ru.timofeev.recservice.dto.rule.RuleStatsDto;
import ru.timofeev.recservice.service.RecommendationRuleService;
import ru.timofeev.recservice.service.RuleStatService;

import java.net.HttpURLConnection;
import java.util.UUID;

@RestController
@RequestMapping("/rule")
@AllArgsConstructor
public class RuleController {

    private final RecommendationRuleService recommendationRuleService;
    private final RuleStatService ruleStatService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все динамические продукты")
    @ApiResponse(responseCode = "200", description = "Список получен")
    public ResponseEntity<GetRuleResponseDto> get() {
        return ResponseEntity.ok(recommendationRuleService.getAllDynamicRecs());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Создать новый продукт")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Продукт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка параметров запроса")
    })
    @ApiResponse(responseCode = "204", description = "Правило успешно удалено")
    public ResponseEntity<ProductDto> post(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(recommendationRuleService.create(productDto));
    }

    @DeleteMapping("{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить продукт по productId")
    @ApiResponse(responseCode = "204", description = "Продукт успешно удалено")
    public ResponseEntity delete(@PathVariable UUID productId) {
        recommendationRuleService.delete(productId);
        return ResponseEntity.status(HttpURLConnection.HTTP_NO_CONTENT).build();
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить статистику срабатывания правил")
    @ApiResponse(responseCode = "200", description = "Список получен")
    public ResponseEntity<RuleStatsDto> getRuleStats() {
        return ResponseEntity.ok(ruleStatService.getAll());
    }
}
