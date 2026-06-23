package ru.timofeev.recservice.component.mapper;

import org.springframework.stereotype.Component;
import ru.timofeev.recservice.component.dynamicRule.RuleConditionDto;
import ru.timofeev.recservice.dto.recommendations.RecommendationDto;
import ru.timofeev.recservice.dto.rule.ProductDto;
import ru.timofeev.recservice.dto.rule.RuleDto;
import ru.timofeev.recservice.model.RecommendationModel;
import ru.timofeev.recservice.model.enums.RecommendationRuleType;

@Component
public class RecommendationMapper {

    public RecommendationDto getRecommendationDtoFromModel(RecommendationModel model) {
        return RecommendationDto
                .builder()
                .name(model.getName())
                .text(model.getDescription())
                .productId(model.getProductId())
                .id(model.getId())
                .build();
    }

    public ProductDto getProductDtoFromModel(RecommendationModel model) {
        return ProductDto
                .builder()
                .productName(model.getName())
                .productText(model.getDescription())
                .productId(model.getProductId())
                .id(model.getId())
                .rule(model.getRuleSet().stream().map(this::getRuleDtoFromModel).toList())
                .build();
    }

    public RuleDto getRuleDtoFromModel(RuleConditionDto model) {
        return RuleDto
                .builder()
                .arguments(model.getArguments())
                .query(model.getQueryType())
                .negate(model.isNegate())
                .build();
    }

    public RecommendationModel getModelFromProductDto(ProductDto productDto) {
        return RecommendationModel
                .builder()
                .name(productDto.getProductName())
                .description(productDto.getProductText())
                .productId(productDto.getProductId())
                .id(productDto.getId())
                .ruleSet(productDto.getRule().stream().map(this::getModelFromRuleDto).toList())
                .ruleType(RecommendationRuleType.DYNAMIC)
                .build();
    }

    public RuleConditionDto getModelFromRuleDto(RuleDto model) {
        return RuleConditionDto
                .builder()
                .arguments(model.getArguments())
                .queryType(model.getQuery())
                .negate(model.isNegate())
                .build();
    }

}
