package ru.semavin.TechRadarPolls.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PollDTO {
    @NotNull
    private int tech_id;
    @NotNull
    private String ringResult;
    @NotNull
    private int user_id;
}
