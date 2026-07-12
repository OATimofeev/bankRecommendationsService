package ru.timofeev.recservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.timofeev.recservice.model.RuleStatModel;
import ru.timofeev.recservice.model.enums.StatNameEnum;

public interface RuleStatRepository extends JpaRepository<RuleStatModel, Long> {

    @Modifying
    @Query("""
            update RuleStatModel rs
            set rs.counter = rs.counter + 1
            where rs.rule.id = :ruleId
              and rs.statName = :statName
            """)
    void incrementCounterByRuleIdAndStatName(@Param("ruleId") Long ruleId,
                                             @Param("statName") StatNameEnum statName);

}
