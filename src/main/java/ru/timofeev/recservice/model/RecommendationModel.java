package ru.timofeev.recservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("recommendations")
@Data
@Builder
public class RecommendationModel {

    @Id
    private UUID id;
    private String name;
    private String description;
    @Column("rule_set")
    private String ruleSet;
    @Column("rule_code")
    private String ruleCode;
}