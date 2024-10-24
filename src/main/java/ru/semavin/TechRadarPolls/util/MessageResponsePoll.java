package ru.semavin.TechRadarPolls.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponsePoll {
    @Builder.Default
    private String code = "200";
    private String message;
}
