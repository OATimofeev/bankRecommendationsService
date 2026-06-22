package ru.timofeev.recservice.component.dynamicRule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleConditionDto {

    private QueryType queryType;
    private List<String> arguments;
    private boolean negate;
}
