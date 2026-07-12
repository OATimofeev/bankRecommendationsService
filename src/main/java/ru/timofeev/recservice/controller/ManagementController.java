package ru.timofeev.recservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.timofeev.recservice.service.TransactionDataService;

import java.net.HttpURLConnection;

@RestController
@RequestMapping("/management")
@AllArgsConstructor
public class ManagementController {

    private final TransactionDataService transactionDataService;
    private final BuildProperties buildProperties;

    @PostMapping("/clear-caches")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Сбросить кэш БД транзакций")
    @ApiResponse(responseCode = "204", description = "Кэш успешно очищен")
    public ResponseEntity invalidateCache() {
        transactionDataService.invalidateCache();
        return ResponseEntity.status(HttpURLConnection.HTTP_NO_CONTENT).build();
    }


    @GetMapping("/info")
    public InfoResponse getInfo() {
        return new InfoResponse(
                buildProperties.getName(),
                buildProperties.getVersion()
        );
    }

    public record InfoResponse(String name, String version) {
    }
}
