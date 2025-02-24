package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.models.Section;
import ru.semavin.TechRadarPolls.repositories.SectionRepository;
import ru.semavin.TechRadarPolls.util.BaseNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    public Section findByName(String section){
        return sectionRepository.findBySecNameIgnoreCase(section)
                .orElseThrow(() -> BaseNotFoundException.create(Section.class));
    }
    public Section findByNameWithListExceptions(String section, List<String> exceptionMessageList){
        if (section == null){
            return null;
        }


        return sectionRepository.findBySecNameIgnoreCase(section).orElseGet(
                () ->
                {
                    exceptionMessageList.add(BaseNotFoundException.create(Section.class).getMessage());
                    return null;
                }
        );
    }
}
