package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.usertype.UserCollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.semavin.TechRadarPolls.dtos.TechnologyGetDTO;
import ru.semavin.TechRadarPolls.dtos.TechnologyPostDTO;
import ru.semavin.TechRadarPolls.models.*;
import ru.semavin.TechRadarPolls.repositories.TechnologyRepository;
import ru.semavin.TechRadarPolls.repositories.TechnologyStatusRepository;
import ru.semavin.TechRadarPolls.util.BaseNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TechnologyService {

    private static final Logger log = LoggerFactory.getLogger(TechnologyService.class);
    private  final TechnologyRepository technologyRepository;
    private final TechnologyStatusRepository technologyStatusRepository;


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
    public List<TechnologyGetDTO> convertToListDto(List<Technology> technologies) {

        if (technologies == null){
            return Collections.emptyList();
        }

        if (technologies.isEmpty()){
            return Collections.emptyList();
        }
        return technologies.stream()
                .map(technology -> {
                    log.info("Mapping technology: {}", technology);
                    return TechnologyGetDTO.builder()
                            .id(technology.getTechId())
                            .name(technology.getName())
                            .description(technology.getDescription())
                            .category(technology.getCategory().getCatName())
                            .ring(technology.getRing().getRingName())
                            .build();
                })
                .collect(Collectors.toList());
    }
    public Technology save(TechnologyPostDTO technology, Ring ring, Category category, Section section) {
        return technologyRepository.save(convertToPostDto(technology, category, ring, section));
    }

    private Technology convertToPostDto(TechnologyPostDTO technology, Category category, Ring ring, Section section) {
        return Technology.builder()
                .category(category)
                .name(technology.getName())
                .description(technology.getDescription())
                .ring(ring)
                .section(section)
                .updateTime(LocalDateTime.now())
                .status(technologyStatusRepository.findByStatusIgnoreCase(technology.getStatuses()).orElseThrow(() -> BaseNotFoundException.create(TechnologyStatus.class)))
                .build();
    }

    public void delete(Integer id) {
        technologyRepository.delete(findOne(id));
    }
    public void archive(Integer id){
        Technology technology = findOne(id);
        technology.setStatus(technologyStatusRepository.findByStatusIgnoreCase("archived").orElseThrow(() -> BaseNotFoundException.create(TechnologyStatus.class)));
        technologyRepository.save(technology);
    }
}
