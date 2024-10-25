package ru.semavin.TechRadarPolls.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO для представления данных о технологии")
public class TechnologyDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String ring;
}
