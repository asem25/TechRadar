package ru.semavin.TechRadarPolls.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.semavin.TechRadarPolls.dtos.PollDTO;
import ru.semavin.TechRadarPolls.dtos.TechnologyGetDTO;
import ru.semavin.TechRadarPolls.dtos.TechnologyPostDTO;
import ru.semavin.TechRadarPolls.dtos.TechnologyWithPollsResultDTO;
import ru.semavin.TechRadarPolls.models.*;
import ru.semavin.TechRadarPolls.services.*;
import ru.semavin.TechRadarPolls.util.*;


import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Главный контроллер",
        description = "Реализует механизм опросов для сервиса ТЕХРАДАР")
public class TechnologyController {
    private final TechnologyService technologyService;
    private final PollService pollService;
    private final RingService ringService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final SectionService sectionService;

    @GetMapping("/api/technology")
    @Operation(summary = "Запрос для формирования техрадара.",
            description = ". Возвращает список всех технологий,\n" +
                    "соответствующих фильтрам (категория, секция), кроме архивированных.")
    public ResponseEntity<Map<String, Object>> findAllByFilters(@RequestParam(required = false) String category,
                                                                @RequestParam(required = false) String section) {
        List<String> errors = new ArrayList<>();
        Section sectionObject = sectionService.findByNameWithListExceptions(section, errors);
        Category categoryObject = categoryService.findByNameWithListExceptions(category, errors);
        if (!errors.isEmpty()) {
            log.error("Controller/api/technology: Map with errors not empty");
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid query parameters");
            response.put("details", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<TechnologyGetDTO> technologies = technologyService.convertToListDto(
                technologyService
                .findAllByFilter(categoryObject, sectionObject));
        log.info("technologies from controller on point /api/technologies " + technologies);

        Map<String, Object> response = new HashMap<>();
        response.put("technologies", technologies);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/dashboard/{tech_id}")
    @Operation(summary = "Запрос на формирование дашборда.",
            description = "Возвращает количество актуальных голосов\n" +
                    "за указанную технологию (последний голос каждого пользователя).")
    public TechnologyWithPollsResultDTO getVotesCountForAllRings(@PathVariable(name = "tech_id") Integer id) throws ErrorResponseServer {
        Technology technology = technologyService.findOne(id);
        TechnologyWithPollsResultDTO resultDTO;
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
                                        BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
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
    @PostMapping("/api/technology")
    public ResponseEntity<String> postTechnology(@RequestBody @Valid TechnologyPostDTO technologyPostDTO){
        Ring ring = ringService.findByName(technologyPostDTO.getRing());
        Category category = categoryService.findByName(technologyPostDTO.getCategory());
        Section section = sectionService.findByName(technologyPostDTO.getSection());
        return ResponseEntity.ok(technologyService.save(technologyPostDTO, ring, category, section).getName() + " saved successful");
    }
    @PatchMapping("/api/technology/archive/{id}")
    public ResponseEntity<String> archiveTechnology(@PathVariable(name = "id") Integer id){
        technologyService.archive(id);
        return ResponseEntity.ok("Technology with id " + id + " archived");
    }
    @DeleteMapping("/api/technology/{id}")
    public ResponseEntity<String> deleteTechnology(@PathVariable(name = "id") Integer id){
        pollService.deleteByTechnology(id);
        technologyService.delete(id);
        return ResponseEntity.ok("Technology " + id + " deleted successfully" );
    }
    private Poll convertPollDtoToPoll(PollDTO pollDTO) {
        Ring ring = ringService.findByName(pollDTO.getRingResult());
        User user = userService.findById(pollDTO.getUser_id());
        Technology technology = technologyService.findOne(pollDTO.getTech_id());
        return Poll.builder().user(user).technology(technology).ring(ring)
                .time(LocalDateTime.now()).build();
    }

}
