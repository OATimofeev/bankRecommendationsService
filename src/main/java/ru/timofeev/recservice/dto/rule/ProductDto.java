package ru.timofeev.recservice.dto.rule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    @NotNull(message = "ProductId must not be null")
    private UUID productId;

    @NotEmpty(message = "RuleSet must not be empty")
    @Valid
    private List<RuleDto> rule;

    @NotBlank(message = "ProductName must not be blank")
    private String productName;

    @NotBlank(message = "ProductText must not be blank")
    private String productText;
}