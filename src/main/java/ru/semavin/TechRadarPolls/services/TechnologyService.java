package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.semavin.TechRadarPolls.dtos.TechnologyDTO;
import ru.semavin.TechRadarPolls.models.Category;
import ru.semavin.TechRadarPolls.models.Section;
import ru.semavin.TechRadarPolls.models.Technology;
import ru.semavin.TechRadarPolls.repositories.TechnologyRepository;
import ru.semavin.TechRadarPolls.util.BaseNotFoundException;
import ru.semavin.TechRadarPolls.util.TechnologyNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TechnologyService {

    private static final Logger log = LoggerFactory.getLogger(TechnologyService.class);
    private  final TechnologyRepository technologyRepository;
    public Technology findOne(Integer techId){
        return technologyRepository.findById(Long.valueOf(techId)).orElseThrow(() -> BaseNotFoundException.create(Technology.class));
    }
    public List<Technology> findAllByFilter(Category category, Section section) {
        if (category != null && section != null) {
            log.info("Calling findByCategoryAndSection with category={} and section={}", category, section);
            List<Technology> result = technologyRepository.findByCategoryAndSection(category, section);
            log.info("findByCategoryAndSection returned: {}", result);
            return result;
        } else if (category != null) {
            log.info("Calling findByCategory with category={}", category);
            List<Technology> result = technologyRepository.findByCategory(category);
            log.info("findByCategory returned: {}", result);
            return result;
        } else if (section != null) {
            log.info("Calling findBySection with section={}", section);
            List<Technology> result = technologyRepository.findBySection(section);
            log.info("findBySection returned: {}", result);
            return result;
        } else {
            log.info("Calling findAll");
            List<Technology> result = technologyRepository.findAll();
            log.info("findAll returned: {}", result);
            return result;
        }
    }
    public Technology findByIdWithListExceptions(Integer techId, List<RuntimeException> exceptions){
        return technologyRepository.findById(Long.valueOf(techId))
                .orElseGet(() -> {
                            exceptions.add(BaseNotFoundException.create(Technology.class));
                            return null;
                        }
                );
    }
    public List<TechnologyDTO> convertToListDto(List<Technology> technologies) {

        if (technologies == null){
            return Collections.emptyList();
        }

        if (technologies.isEmpty()){
            return Collections.emptyList();
        }
        return technologies.stream()
                .map(technology -> {
                    log.info("Mapping technology: {}", technology);
                    return TechnologyDTO.builder()
                            .id(technology.getTechId())
                            .name(technology.getName())
                            .description(technology.getDescription())
                            .category(technology.getCategory().getCatName())
                            .ring(technology.getRing().getRingName())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
