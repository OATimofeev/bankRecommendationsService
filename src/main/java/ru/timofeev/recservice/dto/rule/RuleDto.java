package ru.timofeev.recservice.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.timofeev.recservice.component.dynamicRule.QueryType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleDto {
    private QueryType query;
    private List<String> arguments;
    private boolean negate;
}
