package ru.semavin.TechRadarPolls.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO для вывода сообщения ответа")
public class MessageResponsePoll {
    @Builder.Default
    private String code = "200";
    private String message;
}
