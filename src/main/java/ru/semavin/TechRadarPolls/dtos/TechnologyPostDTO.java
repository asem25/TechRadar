package ru.semavin.TechRadarPolls.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.semavin.TechRadarPolls.models.Section;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnologyPostDTO {
    @NotBlank(message = "Название технологии не должно быть пустым")
    private String name;
    private String description;
    @NotBlank(message = "Категория обязательна")
    private String category;
    @Builder.Default
    private String ring = "HOLD";
    @NotBlank(message = "Секция обязательна")
    private String section;
    @Builder.Default
    private String statuses = "NEW";
}
