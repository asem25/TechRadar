package ru.semavin.TechRadarPolls.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseNewTechnology {
    private Long id;
    private String message;
}
