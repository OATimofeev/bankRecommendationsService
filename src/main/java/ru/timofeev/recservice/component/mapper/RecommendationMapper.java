package ru.timofeev.recservice.component.mapper;

import org.springframework.stereotype.Component;
import ru.timofeev.recservice.dto.GetRecommendationResponseDto;
import ru.timofeev.recservice.model.RecommendationModel;

@Component
public class RecommendationMapper {

    public GetRecommendationResponseDto.RecommendationDto getDtoFromModel(RecommendationModel model) {
        return GetRecommendationResponseDto.RecommendationDto
                .builder()
                .name(model.getName())
                .text(model.getDescription())
                .id(model.getId())
                .build();
    }
}
