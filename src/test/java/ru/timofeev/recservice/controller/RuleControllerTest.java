package ru.timofeev.recservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.timofeev.recservice.dto.rule.GetRuleResponseDto;
import ru.timofeev.recservice.dto.rule.ProductDto;
import ru.timofeev.recservice.dto.rule.RuleStatsDto;
import ru.timofeev.recservice.service.RecommendationService;
import ru.timofeev.recservice.service.RuleStatService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private RuleStatService ruleStatService;

    @InjectMocks
    private RuleController ruleController;

    @Test
    void get_shouldReturnOkResponseWithDynamicRules() {
        GetRuleResponseDto responseDto = GetRuleResponseDto.builder().build();

        when(recommendationService.getAllDynamicRecs()).thenReturn(responseDto);

        ResponseEntity<GetRuleResponseDto> response = ruleController.get();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDto);

        verify(recommendationService).getAllDynamicRecs();
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    void post_shouldReturnOkResponseWithCreatedProduct() {
        ProductDto productDto = ProductDto.builder().build();
        ProductDto createdProduct = ProductDto.builder().build();

        when(recommendationService.create(productDto)).thenReturn(createdProduct);

        ResponseEntity<ProductDto> response = ruleController.post(productDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(createdProduct);

        verify(recommendationService).create(productDto);
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    void delete_shouldReturnNoContent() {
        UUID productId = UUID.randomUUID();

        ResponseEntity<Void> response = ruleController.delete(productId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(recommendationService).delete(productId);
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    void getRuleStats_shouldReturnOkResponseWithStats() {
        RuleStatsDto statsDto = RuleStatsDto.builder().build();

        when(ruleStatService.getAll()).thenReturn(statsDto);

        ResponseEntity<RuleStatsDto> response = ruleController.getRuleStats();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(statsDto);

        verify(ruleStatService).getAll();
        verifyNoMoreInteractions(ruleStatService);
    }
}