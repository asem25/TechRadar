package ru.semavin.TechRadarPolls.dtos;

import lombok.Data;
import ru.semavin.TechRadarPolls.models.Ring;

import java.util.Map;
@Data
public class TechnologyWithPollsResultDTO {
    private Long techId;
    private String category;
    private String section;
    private String name;
    private Map<Ring, Integer> votes;
}
