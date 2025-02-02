package ru.semavin.TechRadarPolls.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.semavin.TechRadarPolls.config.TestConfig;
import ru.semavin.TechRadarPolls.controllers.TechnologyController;
import ru.semavin.TechRadarPolls.dtos.PollDTO;
import ru.semavin.TechRadarPolls.models.*;
import ru.semavin.TechRadarPolls.services.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TechnologyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
class TechnologyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TechnologyService technologyService;

    @Autowired
    private PollService pollService;

    @Autowired
    private RingService ringService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Technology technology;
    private Category category;
    private Section section;
    private Ring ring;
    private PollDTO pollDTO;

    @BeforeEach
    void setup() {
        category = Category.builder()
                .cat_id(1L)
                .catName("BACKEND")
                .build();

        section = Section.builder()
                .sec_id(2L)
                .secName("TOOLS")
                .build();

        ring = Ring.builder()
                .ring_id(1L)
                .ringName("ADOPT")
                .build();

        technology = Technology.builder()
                .techId(1L)
                .name("Spring Boot")
                .description("Framework for building Java applications.")
                .category(category)
                .section(section)
                .ring(ring)
                .build();

        pollDTO = PollDTO.builder()
                .user_id(1)
                .tech_id(1)
                .ringResult("ADOPT")
                .build();
    }

    // Тест: GET /api/technology – корректные параметры
    @Test
    void testFindAllByFiltersSuccess() throws Exception {
        given(categoryService.findByName("Software")).willReturn(Optional.of(category));
        given(sectionService.findByName("Backend")).willReturn(Optional.of(section));
        given(technologyService.findAllByFilter(category, section)).willReturn(List.of(technology));

        mockMvc.perform(get("/api/technology")
                        .param("category", "Software")
                        .param("section", "Backend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technologies", hasSize(1)))
                .andExpect(jsonPath("$.technologies[0].name", is("Spring Boot")))
                .andExpect(jsonPath("$.technologies[0].category", is("BACKEND")))
                .andExpect(jsonPath("$.technologies[0].ring", is("ADOPT")));
    }

    // Тест: GET /api/technology – неверные параметры (не найдена категория)
    @Test
    void testFindAllByFiltersInvalidParameters() throws Exception {
        given(categoryService.findByName("Unknown")).willReturn(Optional.empty());
        given(sectionService.findByName("Backend")).willReturn(Optional.of(section));

        mockMvc.perform(get("/api/technology")
                        .param("category", "Unknown")
                        .param("section", "Backend"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid query parameters")))
                .andExpect(jsonPath("$.details.category", is("CATEGORY NOT FOUND")));
    }

    // Тест: GET /api/dashboard/{tech_id} – успешное получение дашборда
    @Test
    void testGetVotesCountForAllRingsSuccess() throws Exception {
        Map<String, Integer> votes = new HashMap<>();
        votes.put("ADOPT", 10);
        votes.put("TRIAL", 5);

        given(technologyService.findOne(1)).willReturn(Optional.of(technology));
        given(pollService.countUsersForTechByAllRings(1)).willReturn(votes);

        mockMvc.perform(get("/api/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.techId", is(1)))
                .andExpect(jsonPath("$.name", is("Spring Boot")))
                .andExpect(jsonPath("$.category", is("BACKEND")))
                .andExpect(jsonPath("$.section", is("TOOLS")))
                .andExpect(jsonPath("$.votes.ADOPT", is(10)))
                .andExpect(jsonPath("$.votes.TRIAL", is(5)));
    }

    // Тест: GET /api/dashboard/{tech_id} – технология не найдена
    @Test
    void testGetVotesCountForAllRingsTechnologyNotFound() throws Exception {
        given(technologyService.findOne(99)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/dashboard/99"))
                .andExpect(status().isNotFound());
    }

    // Тест: POST /poll – успешное добавление опроса
    @Test
    void testSendPollSuccess() throws Exception {
        given(ringService.findByName("ADOPT")).willReturn(Optional.of(ring));
        given(userService.findById(1)).willReturn(Optional.of(User.builder().userId(1L).build()));
        given(technologyService.findOne(1)).willReturn(Optional.of(technology));


        mockMvc.perform(post("/poll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pollDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Результат опроса успешно добавлен")));
    }

    // Тест: POST /poll – ошибка валидации (например, отсутствует ringResult)
    @Test
    void testSendPollBadRequest() throws Exception {
        pollDTO.setRingResult(null); // некорректные данные

        mockMvc.perform(post("/poll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pollDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("400")))
                .andExpect(jsonPath("$.message", is("BAD REQUEST")));
    }

    // Тест: POST /poll – внутренняя ошибка сервера при сохранении опроса
    @Test
    void testSendPollInternalServerError() throws Exception {
        given(ringService.findByName("ADOPT")).willReturn(Optional.of(ring));
        given(userService.findById(1)).willReturn(Optional.of(User.builder().userId(1L).build()));
        given(technologyService.findOne(1)).willReturn(Optional.of(technology));
        BDDMockito.willThrow(new RuntimeException("DB error"))
                .given(pollService).save(any(Poll.class));

        mockMvc.perform(post("/poll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pollDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("500")))
                .andExpect(jsonPath("$.message", is("INTERNAL_SERVER_ERROR")));
    }
}

