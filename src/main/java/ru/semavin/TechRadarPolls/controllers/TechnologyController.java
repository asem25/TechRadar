package ru.semavin.TechRadarPolls.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.semavin.TechRadarPolls.dtos.TechnologyWithPollsResultDTO;
import ru.semavin.TechRadarPolls.models.Technology;
import ru.semavin.TechRadarPolls.services.PollService;
import ru.semavin.TechRadarPolls.services.TechnologyService;


import java.util.List;
@RestController
@RequiredArgsConstructor
//TODO Обрабатоть все случаи ошибок(например, когда параметры заданы неверно)
public class TechnologyController {


    private final TechnologyService technologyService;
    private final PollService pollService;
    private final ModelMapper modelMapper;

    @GetMapping("/api/technology")
    public List<Technology> findAllByFilters(){
        //TODO Добавить exception
        return technologyService.findAll();
    }

    @GetMapping("/api/dashboard/{tech_id}")
    public TechnologyWithPollsResultDTO getVotesCountForAllRings(@PathVariable(name = "tech_id") Integer id) {

        Technology technology = technologyService.findOne(id).orElse(null);
        TechnologyWithPollsResultDTO resultDTO = modelMapper.map(technology, TechnologyWithPollsResultDTO.class);
        resultDTO.setVotes(pollService.countUsersForTechByAllRings(id));
        return resultDTO;
    }
    @PostMapping("/poll")
    //TODO Сделать голосование и учесть, что
//    Только явно не доработан. Поэтому стоит добавить параметр "идентификатор опроса" и "идентификатор пользователя"
    public void sendGolos(){}
}
