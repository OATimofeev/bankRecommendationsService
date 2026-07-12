package ru.timofeev.recservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.timofeev.recservice.model.enums.StatNameEnum;

import java.util.UUID;

@Entity
@Table(name = "rule_stat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_rule_stat_rule_id_stat_name",
                        columnNames = {"RULE_ID", "STAT_NAME"}
                )
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleStatModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "RULE_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_rule_stat_recommendation")
    )
    private RecommendationModel rule;

    @Builder.Default
    @Column(name = "COUNTER", nullable = false)
    private Long counter = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "STAT_NAME", nullable = false)
    private StatNameEnum statName;
}