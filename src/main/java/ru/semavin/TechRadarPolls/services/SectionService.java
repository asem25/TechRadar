package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.models.Section;
import ru.semavin.TechRadarPolls.repositories.SectionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    public Optional<Section> findByName(String section){
        return sectionRepository.findBySecNameIgnoreCase(section);
    }
}
