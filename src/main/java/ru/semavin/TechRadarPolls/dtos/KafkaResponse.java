package ru.semavin.TechRadarPolls.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaResponse {
    private String eventType;
    private String text;
    private String timestamp;

    @Override
    public String toString() {
        return String.format("""
                EventType: %s
                Text: %s
                Time of response: %s;              \s
                """, eventType, text, timestamp);
    }
}
