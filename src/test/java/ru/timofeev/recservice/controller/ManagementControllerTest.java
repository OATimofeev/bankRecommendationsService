package ru.timofeev.recservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.timofeev.recservice.service.TransactionDataService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagementControllerTest {

    @Mock
    private TransactionDataService transactionDataService;

    @Mock
    private BuildProperties buildProperties;

    @InjectMocks
    private ManagementController managementController;

    @Test
    void invalidateCache_shouldReturnNoContent() {
        ResponseEntity<Void> response = managementController.invalidateCache();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(transactionDataService).invalidateCache();
        verifyNoMoreInteractions(transactionDataService);
    }

    @Test
    void getInfo_shouldReturnApplicationInfo() {
        when(buildProperties.getName()).thenReturn("rec-service");
        when(buildProperties.getVersion()).thenReturn("1.0.0");

        ManagementController.InfoResponse response = managementController.getInfo();

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("rec-service");
        assertThat(response.version()).isEqualTo("1.0.0");

        verify(buildProperties).getName();
        verify(buildProperties).getVersion();
        verifyNoMoreInteractions(buildProperties);
    }
}