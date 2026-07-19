package ru.timofeev.recservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.timofeev.recservice.service.TransactionDataService;

/**
 * Контроллер служебных management-эндпоинтов приложения.
 * <p>
 * Предоставляет операции для получения информации о сервисе
 * и сброса внутреннего кэша данных транзакций.
 */
@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
public class ManagementController {

    private final TransactionDataService transactionDataService;
    private final BuildProperties buildProperties;

    /**
     * Очищает кэш транзакционных данных.
     *
     * @return HTTP-ответ без тела со статусом 204 No Content
     */
    @PostMapping("/clear-caches")
    @Operation(summary = "Сбросить кэш БД транзакций")
    @ApiResponse(responseCode = "204", description = "Кэш успешно очищен")
    public ResponseEntity<Void> invalidateCache() {
        transactionDataService.invalidateCache();
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает краткую информацию о приложении,
     * сформированную на основе build metadata.
     *
     * @return объект с именем и версией сервиса
     */
    @GetMapping("/info")
    @Operation(summary = "Получить информацию о сервисе")
    @ApiResponse(responseCode = "200", description = "Информация о приложении успешно получена")
    public InfoResponse getInfo() {
        return new InfoResponse(
                buildProperties.getName(),
                buildProperties.getVersion()
        );
    }

    /**
     * DTO для ответа management-эндпоинта с информацией о приложении.
     *
     * @param name    имя приложения
     * @param version версия приложения
     */
    public record InfoResponse(String name, String version) {
    }
}