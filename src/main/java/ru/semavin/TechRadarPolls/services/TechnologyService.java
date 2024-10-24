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

@RequiredArgsConstructor
public class TechnologyService {

    private  final TechnologyRepository technologyRepository;
    @Transactional
    public List<Technology> findAll(){
        return technologyRepository.findAll();
    }
    @Transactional
    public void save(Technology technology){
        technologyRepository.save(technology);
    }

    @Transactional
    public Optional<Technology> findOne(Integer techId){
        return technologyRepository.findById(Long.valueOf(techId));
    }
    @Transactional
    public Technology addTechnology(Technology technology){
        return technologyRepository.save(technology);
    }
}
