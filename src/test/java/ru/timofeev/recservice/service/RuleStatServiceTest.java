package ru.timofeev.recservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.timofeev.recservice.component.mapper.RuleStatsMapper;
import ru.timofeev.recservice.dto.rule.RuleStatDto;
import ru.timofeev.recservice.dto.rule.RuleStatsDto;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.RuleStatModel;
import ru.timofeev.recservice.model.enums.StatNameEnum;
import ru.timofeev.recservice.repository.RuleStatRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleStatServiceTest {

    @Mock
    private RuleStatRepository ruleStatRepository;

    @Mock
    private RuleStatsMapper ruleStatsMapper;

    @InjectMocks
    private RuleStatService ruleStatService;

    @Test
    void incrementStat_shouldIncrementCounterForEachRule() {
        RecommendationModel firstRule = RecommendationModel.builder()
                .id(1L)
                .productId(UUID.randomUUID())
                .build();

        RecommendationModel secondRule = RecommendationModel.builder()
                .id(2L)
                .productId(UUID.randomUUID())
                .build();

        List<RecommendationModel> rules = List.of(firstRule, secondRule);

        ruleStatService.incrementStat(rules, StatNameEnum.TRIGGERED);

        verify(ruleStatRepository).incrementCounterByRuleIdAndStatName(1L, StatNameEnum.TRIGGERED);
        verify(ruleStatRepository).incrementCounterByRuleIdAndStatName(2L, StatNameEnum.TRIGGERED);
        verifyNoMoreInteractions(ruleStatRepository);
    }

    @Test
    void create_shouldSaveInitialStatForRule() {
        RecommendationModel rule = RecommendationModel.builder()
                .id(10L)
                .productId(UUID.randomUUID())
                .build();

        ruleStatService.create(rule);

        verify(ruleStatRepository).save(
                RuleStatModel.builder()
                        .rule(rule)
                        .statName(StatNameEnum.TRIGGERED)
                        .build()
        );
    }

    @Test
    void delete_shouldDeleteStatsByRuleId() {
        RecommendationModel rule = RecommendationModel.builder()
                .id(5L)
                .productId(UUID.randomUUID())
                .build();

        ruleStatService.delete(rule);

        verify(ruleStatRepository).deleteByRuleId(5L);
    }

    @Test
    void getAll_shouldReturnMappedStatsDto() {
        RuleStatModel firstStat = RuleStatModel.builder()
                .id(UUID.randomUUID())
                .build();

        RuleStatModel secondStat = RuleStatModel.builder()
                .id(UUID.randomUUID())
                .build();

        RuleStatDto firstDto = RuleStatDto.builder()
                .ruleId(1L)
                .count(10L)
                .build();

        RuleStatDto secondDto = RuleStatDto.builder()
                .ruleId(2L)
                .count(20L)
                .build();

        when(ruleStatRepository.findAll()).thenReturn(List.of(firstStat, secondStat));
        when(ruleStatsMapper.getRecommendationDtoFromModel(firstStat)).thenReturn(firstDto);
        when(ruleStatsMapper.getRecommendationDtoFromModel(secondStat)).thenReturn(secondDto);

        RuleStatsDto result = ruleStatService.getAll();

        assertThat(result).isNotNull();
        assertThat(result.getStats())
                .hasSize(2)
                .containsExactly(firstDto, secondDto);

        verify(ruleStatRepository).findAll();
        verify(ruleStatsMapper).getRecommendationDtoFromModel(firstStat);
        verify(ruleStatsMapper).getRecommendationDtoFromModel(secondStat);
    }
}