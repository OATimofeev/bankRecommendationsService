package ru.timofeev.recservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.component.mapper.RuleStatsMapper;
import ru.timofeev.recservice.dto.rule.RuleStatsDto;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.RuleStatModel;
import ru.timofeev.recservice.model.enums.StatNameEnum;
import ru.timofeev.recservice.repository.RuleStatRepository;

import java.util.List;

/**
 * Сервис для работы со статистикой срабатывания правил рекомендаций.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RuleStatService {

    private final RuleStatRepository ruleStatRepository;
    private final RuleStatsMapper ruleStatsMapper;

    /**
     * Увеличивает счётчик статистики для переданного набора правил.
     *
     * @param rules список правил
     * @param stat  тип статистики
     */
    @Transactional
    public void incrementStat(List<RecommendationModel> rules, StatNameEnum stat) {
        rules.forEach(rule -> {
            log.info("Increment stats for rule id = {}", rule.getId());
            ruleStatRepository.incrementCounterByRuleIdAndStatName(rule.getId(), stat);
        });
    }

    /**
     * Создаёт начальную запись статистики для нового правила.
     *
     * @param createdRule созданное правило рекомендации
     */
    public void create(RecommendationModel createdRule) {
        log.info("Was invoked method for create new rule stat for product id = {}", createdRule.getProductId());
        ruleStatRepository.save(
                RuleStatModel.builder()
                        .rule(createdRule)
                        .statName(StatNameEnum.TRIGGERED)
                        .build()
        );
    }

    /**
     * Удаляет статистику по идентификатору правила.
     *
     * @param recommendation правило, для которого нужно удалить статистику
     */
    @Transactional
    public void delete(RecommendationModel recommendation) {
        log.info("Was invoked method for delete rule stats by rule id = {}", recommendation.getId());
        ruleStatRepository.deleteByRuleId(recommendation.getId());
    }

    /**
     * Возвращает всю статистику по правилам.
     *
     * @return DTO со списком статистики
     */
    public RuleStatsDto getAll() {
        log.info("Was invoked method for get all triggered rule stats");
        return RuleStatsDto.builder()
                .stats(ruleStatRepository.findAll()
                        .stream()
                        .map(ruleStatsMapper::getRecommendationDtoFromModel)
                        .toList())
                .build();
    }
}