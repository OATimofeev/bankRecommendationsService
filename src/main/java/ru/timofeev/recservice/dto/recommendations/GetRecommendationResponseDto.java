package ru.timofeev.recservice.dto.recommendations;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetRecommendationResponseDto {

    private UUID userId;
    @Builder.Default
    private List<RecommendationDto> recommendations = new ArrayList<>();
}
