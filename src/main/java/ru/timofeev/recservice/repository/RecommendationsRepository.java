package ru.timofeev.recservice.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.timofeev.recservice.model.RecommendationModel;

import java.util.List;
import java.util.UUID;

@Repository
public class RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RecommendationModel> findAll() {
        return jdbcTemplate.query("""
                SELECT * FROM recommendations
                """, (rs, rowNum) ->
                RecommendationModel
                        .builder()
                        .id(rs.getObject("id", UUID.class))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .ruleSet(rs.getString("rule_set"))
                        .ruleCode(rs.getString("rule_code"))
                        .build()
        );
    }

    public RecommendationModel getRecommendationByRuleCode(String ruleCode) {
        return jdbcTemplate.queryForObject("""
                        SELECT * FROM recommendations WHERE rule_code = ?
                        """, (rs, rowNum) ->
                        RecommendationModel
                                .builder()
                                .id(rs.getObject("id", UUID.class))
                                .name(rs.getString("name"))
                                .description(rs.getString("description"))
                                .ruleSet(rs.getString("rule_set"))
                                .ruleCode(rs.getString("rule_code"))
                                .build(),
                ruleCode
        );
    }

}