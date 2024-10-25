package ru.semavin.TechRadarPolls.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.semavin.TechRadarPolls.dtos.PollDTO;
import ru.semavin.TechRadarPolls.dtos.TechnologyDTO;
import ru.semavin.TechRadarPolls.dtos.TechnologyWithPollsResultDTO;
import ru.semavin.TechRadarPolls.models.*;
import ru.semavin.TechRadarPolls.services.*;
import ru.semavin.TechRadarPolls.util.*;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Главный контроллер",
        description = "Реализует механизм опросов для сервиса ТЕХРАДАР" )
public class TechnologyController {
    private final TechnologyService technologyService;
    private final PollService pollService;
    private final ModelMapper modelMapper;
    private final RingService ringService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final SectionService sectionService;
    @GetMapping("/api/technology")
    @Operation(summary = "Запрос для формирования техрадара.",
                description = ". Возвращает список всех технологий,\n" +
                        "соответствующих фильтрам (категория, секция), кроме архивированных.")
    public ResponseEntity<Map<String, Object>> findAllByFilters(@RequestParam(required = false) String category,
                                             @RequestParam(required = false) String section){
        Map<String, String> errors = new HashMap<>();
        Section sectionObject = null;
        if (section != null) {
             sectionObject = sectionService.findByName(section)
                    .orElseGet(() -> {
                        errors.put("section", "SECTION NOT FOUND");
                        return null;
                    });
             log.info("found sectionObject: " + sectionObject);
        }
        Category categoryObject = null;
        if (category != null) {
             categoryObject = categoryService.findByName(category)
                    .orElseGet(() -> {
                        errors.put("category", "CATEGORY NOT FOUND");
                        return null;
                    });
            log.info("found sectionObject: " + categoryObject);
        }

        if (!errors.isEmpty()) {
            log.error("Controller/api/technology: Map with errors not empty");
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid query parameters");
            response.put("details", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        List<TechnologyDTO> technologies = convertToListDto(technologyService
                .findAllByFilter(categoryObject, sectionObject));


        Map<String, Object> response = new HashMap<>();
        response.put("technologies", technologies);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/dashboard/{tech_id}")
    @Operation(summary = "Запрос на формирование дашборда.",
            description = "Возвращает количество актуальных голосов\n" +
                    "за указанную технологию (последний голос каждого пользователя).")
    public TechnologyWithPollsResultDTO getVotesCountForAllRings(@PathVariable(name = "tech_id") Integer id) throws ErrorResponseServer {
        Technology technology = technologyService.findOne(id)
                .orElseThrow(() -> new TechnologyNotFoundException("TECHNOLOGY NOT FOUND"));
        TechnologyWithPollsResultDTO resultDTO = null;
        try {
            resultDTO = TechnologyWithPollsResultDTO.builder()
                    .techId(technology.getTechId())
                    .name(technology.getName())
                    .category(technology.getCategory().getCatName())
                    .section(technology.getSection().getSecName())
                    .votes(pollService.countUsersForTechByAllRings(id))
                    .build();
        } catch (Exception e) {
            log.error("Controller/api/dashboard:Error occurred on the server");
            throw new ErrorResponseServer("An unexpected error occurred on the server. Please try again later.");
        }
        log.info("Controller/api/dashboard/" + id + ":return result info: " + resultDTO);
        return resultDTO;
    }
    @PostMapping("/poll")
    @Operation(summary = "Запрос на добавление результата опроса для указанной технологии.",
            description = " Данные добавляются в\n" +
                    "таблицу опросников для указанной технологии.")
    public MessageResponsePoll sendPoll(@RequestBody @Valid PollDTO pollDTO,
                                        BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()){
                log.warn("Controller/poll: code 400");
                return MessageResponsePoll.builder().code("400")
                        .message("BAD REQUEST")
                        .build();
            }
            log.info("Controller/poll: poll save:" + pollDTO);
            pollService.save(convertPollDtoToPoll(pollDTO));
        } catch (Exception e) {
            log.warn("Controller/poll: code 500");
            return MessageResponsePoll.builder().code("500")
                    .message("INTERNAL_SERVER_ERROR")
                    .build();
        }
        return MessageResponsePoll.builder()
                .message("Результат опроса успешно добавлен")
                .build();
    }
    private Poll convertPollDtoToPoll(PollDTO pollDTO){
        Ring ring = ringService.findByName(pollDTO.getRingResult())
                .orElseThrow(() -> new RingNotFoundException("RING NOT FOUND"));
        User user = userService.findById(pollDTO.getUser_id())
                .orElseThrow(() -> new UserNotFoundException("USER NOT FOUND"));
        Technology technology = technologyService.findOne(pollDTO.getTech_id())
                .orElseThrow(() -> new TechnologyNotFoundException("TECHNOLOGY NOT FOUND"));
        return Poll.builder().user(user).technology(technology).ring(ring)
                .time(LocalDateTime.now()).build();
    }
    private List<TechnologyDTO> convertToListDto(List<Technology> technologies){
        return technologies.stream().map(technology -> TechnologyDTO.builder()
                .id(technology.getTechId())
                .name(technology.getName())
                .description(technology.getDescription())
                .category(technology.getCategory().getCatName())
                .ring(technology.getRing().getRingName())
                .build())
                .collect(Collectors.toList());
    }
}
