package ru.timofeev.recservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetRecommendationResponseDto {

    private UUID userId;
    @Builder.Default
    private List<RecommendationDto> recommendations = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationDto {
        private Long id;
        private UUID productId;
        private String name;
        private String text;
    }

}
