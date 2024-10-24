package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.semavin.TechRadarPolls.models.Category;
import ru.semavin.TechRadarPolls.models.Section;
import ru.semavin.TechRadarPolls.models.Technology;
import ru.semavin.TechRadarPolls.repositories.TechnologyRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TechnologyService {

    private  final TechnologyRepository technologyRepository;
    public Optional<Technology> findOne(Integer techId){
        return technologyRepository.findById(Long.valueOf(techId));
    }
    public List<Technology> findAllByFilter(Category category, Section section) {
        if (category != null && section != null) {
            return technologyRepository.findByCategoryAndSection(category, section);
        } else if (category != null) {
            return technologyRepository.findByCategory(category);
        } else if (section != null) {
            return technologyRepository.findBySection(section);
        } else {
            return technologyRepository.findAll();
        }
    }
}
