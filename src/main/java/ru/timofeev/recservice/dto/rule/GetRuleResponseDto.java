package ru.timofeev.recservice.dto.rule;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class GetRuleResponseDto {

    @Builder.Default
    private List<ProductDto> data = new ArrayList<>();
}
