package ru.timofeev.recservice.dto.recommendations;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    private Long id;
    private UUID productId;
    private String name;
    private String text;
}