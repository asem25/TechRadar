package ru.semavin.TechRadarPolls.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TechnologyDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String ring;
}
