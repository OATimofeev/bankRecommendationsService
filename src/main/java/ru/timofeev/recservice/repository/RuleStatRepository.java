package ru.timofeev.recservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.timofeev.recservice.model.RuleStatModel;
import ru.timofeev.recservice.model.enums.StatNameEnum;

import java.util.UUID;

/**
 * Репозиторий для работы со статистикой срабатывания правил рекомендаций.
 * <p>
 * Позволяет инкрементировать счётчик статистики и удалять записи
 * по идентификатору связанного правила.
 */
public interface RuleStatRepository extends JpaRepository<RuleStatModel, UUID> {

    /**
     * Увеличивает счётчик статистики на 1 для указанного правила и типа статистики.
     *
     * @param ruleId   идентификатор правила рекомендации
     * @param statName тип статистики
     */
    @Modifying
    @Query("""
            update RuleStatModel rs
            set rs.counter = rs.counter + 1
            where rs.rule.id = :ruleId
              and rs.statName = :statName
            """)
    void incrementCounterByRuleIdAndStatName(@Param("ruleId") Long ruleId,
                                             @Param("statName") StatNameEnum statName);

    /**
     * Удаляет все записи статистики для указанного правила рекомендации.
     *
     * @param ruleId идентификатор правила рекомендации
     */
    @Modifying
    @Query("""
            delete from RuleStatModel rs
            where rs.rule.id = :ruleId
            """)
    void deleteByRuleId(@Param("ruleId") Long ruleId);
}