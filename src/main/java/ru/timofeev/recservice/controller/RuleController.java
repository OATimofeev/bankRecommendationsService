package ru.timofeev.recservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.timofeev.recservice.dto.rule.GetRuleResponseDto;
import ru.timofeev.recservice.dto.rule.ProductDto;
import ru.timofeev.recservice.dto.rule.RuleStatsDto;
import ru.timofeev.recservice.service.RecommendationService;
import ru.timofeev.recservice.service.RuleStatService;

import java.util.UUID;

/**
 * REST-контроллер для управления динамическими правилами рекомендаций.
 * <p>
 * Предоставляет эндпоинты для просмотра, создания и удаления динамических
 * продуктов, а также для получения статистики срабатывания правил.
 */
@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class RuleController {

    private final RecommendationService recommendationService;
    private final RuleStatService ruleStatService;

    /**
     * Возвращает список всех динамических продуктов.
     *
     * @return список динамических правил/продуктов
     */
    @GetMapping
    @Operation(summary = "Получить все динамические продукты")
    @ApiResponse(responseCode = "200", description = "Список динамических продуктов успешно получен")
    public ResponseEntity<GetRuleResponseDto> get() {
        return ResponseEntity.ok(recommendationService.getAllDynamicRecs());
    }

    /**
     * Создает новый динамический продукт.
     *
     * @param productDto DTO с параметрами нового продукта и его правил
     * @return созданный продукт
     */
    @PostMapping
    @Operation(summary = "Создать новый продукт")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Продукт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка параметров запроса")
    })
    public ResponseEntity<ProductDto> post(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(recommendationService.create(productDto));
    }

    /**
     * Удаляет динамический продукт по его идентификатору.
     *
     * @param productId идентификатор продукта
     * @return HTTP-ответ без тела со статусом 204 No Content
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "Удалить продукт по productId")
    @ApiResponse(responseCode = "204", description = "Продукт успешно удален")
    public ResponseEntity<Void> delete(@PathVariable UUID productId) {
        recommendationService.delete(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает статистику срабатывания правил.
     *
     * @return статистика по динамическим правилам
     */
    @GetMapping("/stats")
    @Operation(summary = "Получить статистику срабатывания правил")
    @ApiResponse(responseCode = "200", description = "Статистика успешно получена")
    public ResponseEntity<RuleStatsDto> getRuleStats() {
        return ResponseEntity.ok(ruleStatService.getAll());
    }
}