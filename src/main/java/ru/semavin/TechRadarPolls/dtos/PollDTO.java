package ru.semavin.TechRadarPolls.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO для представления данных опроса")
public class PollDTO {
    @NotNull
    private int tech_id;
    @NotNull
    private String ringResult;
    @NotNull
    private int user_id;
}
