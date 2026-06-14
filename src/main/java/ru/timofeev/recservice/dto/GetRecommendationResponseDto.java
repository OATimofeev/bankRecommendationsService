package ru.timofeev.recservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
public class GetRecommendationResponseDto {

    private UUID userId;
    private List<RecommendationDto> recommendations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationDto {
        private UUID id;
        private String name;
        private String text;
    }

}
