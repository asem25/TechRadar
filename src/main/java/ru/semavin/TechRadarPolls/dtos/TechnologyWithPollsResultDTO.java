package ru.semavin.TechRadarPolls.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;


import java.util.Map;
@Data
@Builder
@Schema(description = "DTO для предоставления данных о технологии и результатов опроса")
public class TechnologyWithPollsResultDTO {
    private Long techId;
    private String category;
    private String section;
    private String name;
    private Map<String, Integer> votes;
}
