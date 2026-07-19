package ru.timofeev.recservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.timofeev.recservice.component.mapper.RecommendationMapper;
import ru.timofeev.recservice.dto.recommendations.GetRecommendationResponseDto;
import ru.timofeev.recservice.dto.recommendations.RecommendationDto;
import ru.timofeev.recservice.dto.rule.GetRuleResponseDto;
import ru.timofeev.recservice.dto.rule.ProductDto;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.enums.RecommendationRuleType;
import ru.timofeev.recservice.model.enums.StatNameEnum;
import ru.timofeev.recservice.repository.RecommendationsRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationsRepository recommendationsRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private RuleStatService ruleStatService;

    @Mock
    private TransactionDataService transactionDataService;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_shouldMergeStaticAndDynamicAndIncrementStats() {
        UUID userId = UUID.randomUUID();

        // static: список id правил
        List<Long> staticRuleIds = List.of(1L);
        RecommendationModel staticModel = RecommendationModel.builder().id(1L).build();
        RecommendationDto staticDto = RecommendationDto.builder().id(1L).build();

        when(transactionDataService.getMatchedStaticRules(userId))
                .thenReturn(staticRuleIds);
        when(recommendationsRepository.findById(1L))
                .thenReturn(Optional.of(staticModel));
        when(recommendationMapper.getRecommendationDtoFromModel(staticModel))
                .thenReturn(staticDto);

        // dynamic: список моделей
        RecommendationModel dynamicModel = RecommendationModel.builder().id(2L).build();
        RecommendationDto dynamicDto = RecommendationDto.builder().id(2L).build();

        when(recommendationsRepository.findAllByRuleType(RecommendationRuleType.DYNAMIC))
                .thenReturn(List.of(dynamicModel));
        when(transactionDataService.evaluateDynamicRules(userId, List.of(dynamicModel)))
                .thenReturn(List.of(dynamicModel));
        when(recommendationMapper.getRecommendationDtoFromModel(dynamicModel))
                .thenReturn(dynamicDto);

        List<RecommendationDto> result = recommendationService.getRecommendations(userId);

        assertThat(result)
                .hasSize(2)
                .containsExactly(staticDto, dynamicDto);

        verify(ruleStatService)
                .incrementStat(anyList(), eq(StatNameEnum.TRIGGERED));
    }

    @Test
    void getRecommendationsResponseDto_shouldWrapResultAndUserId() {
        UUID userId = UUID.randomUUID();

        when(transactionDataService.getMatchedStaticRules(userId))
                .thenReturn(List.of());

        when(recommendationsRepository.findAllByRuleType(RecommendationRuleType.DYNAMIC))
                .thenReturn(List.of());

        when(transactionDataService.evaluateDynamicRules(userId, List.of()))
                .thenReturn(List.of());

        GetRecommendationResponseDto responseDto = recommendationService.getRecommendationsResponseDto(userId);

        assertThat(responseDto.getUserId()).isEqualTo(userId);
        assertThat(responseDto.getRecommendations()).isEmpty();

        verify(ruleStatService).incrementStat(anyList(), eq(StatNameEnum.TRIGGERED));
    }

    @Test
    void getAllDynamicRecs_shouldReturnMappedDynamicRecommendations() {
        RecommendationModel model = RecommendationModel.builder().id(1L).build();
        ProductDto productDto = ProductDto.builder().id(1L).build();

        when(recommendationsRepository.findAllByRuleType(RecommendationRuleType.DYNAMIC))
                .thenReturn(List.of(model));
        when(recommendationMapper.getProductDtoFromModel(model))
                .thenReturn(productDto);

        GetRuleResponseDto responseDto = recommendationService.getAllDynamicRecs();

        assertThat(responseDto.getData())
                .hasSize(1)
                .containsExactly(productDto);
    }

    @Test
    void create_shouldSaveRuleAndCreateStats() {
        UUID productId = UUID.randomUUID();
        ProductDto input = ProductDto.builder().id(999L).productId(productId).build();
        RecommendationModel modelToSave = RecommendationModel.builder().productId(productId).build();
        RecommendationModel savedModel = RecommendationModel.builder().id(1L).productId(productId).build();
        ProductDto resultDto = ProductDto.builder().id(1L).productId(productId).build();

        when(recommendationMapper.getModelFromProductDto(input))
                .thenReturn(modelToSave);
        when(recommendationsRepository.save(modelToSave))
                .thenReturn(savedModel);
        when(recommendationMapper.getProductDtoFromModel(savedModel))
                .thenReturn(resultDto);

        ProductDto result = recommendationService.create(input);

        // id должен быть перезатёрт и взят из сохранённой сущности
        assertThat(result.getId()).isEqualTo(1L);

        verify(ruleStatService).create(savedModel);
    }

    @Test
    void delete_shouldDeleteDynamicRuleAndStatsIfPresent() {
        UUID productId = UUID.randomUUID();
        RecommendationModel model = RecommendationModel.builder().id(1L).build();

        when(recommendationsRepository.findByProductIdAndRuleType(productId, RecommendationRuleType.DYNAMIC))
                .thenReturn(Optional.of(model));

        recommendationService.delete(productId);

        verify(ruleStatService).delete(model);
        verify(recommendationsRepository).delete(model);
    }

    @Test
    void delete_shouldDoNothingIfRuleNotFound() {
        UUID productId = UUID.randomUUID();

        when(recommendationsRepository.findByProductIdAndRuleType(productId, RecommendationRuleType.DYNAMIC))
                .thenReturn(Optional.empty());

        recommendationService.delete(productId);

        verifyNoInteractions(ruleStatService);
        verify(recommendationsRepository, never()).delete(any());
    }
}