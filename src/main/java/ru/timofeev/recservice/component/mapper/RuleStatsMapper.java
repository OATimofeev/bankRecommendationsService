package ru.timofeev.recservice.component.mapper;

import org.springframework.stereotype.Component;
import ru.timofeev.recservice.dto.rule.RuleStatDto;
import ru.timofeev.recservice.model.RuleStatModel;

@Component
public class RuleStatsMapper {

    public RuleStatDto getRecommendationDtoFromModel(RuleStatModel model) {
        return RuleStatDto
                .builder()
                .ruleId(model.getRule().getId())
                .count(model.getCounter())
                .build();
    }
}
