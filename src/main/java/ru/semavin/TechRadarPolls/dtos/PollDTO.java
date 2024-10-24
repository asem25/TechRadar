package ru.semavin.TechRadarPolls.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PollDTO {
    @NotNull
    private int tech_id;
    @NotNull
    private String ringResult;
    @NotNull
    private int user_id;
}
