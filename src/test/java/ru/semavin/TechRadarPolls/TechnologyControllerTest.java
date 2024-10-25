package ru.semavin.TechRadarPolls;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.semavin.TechRadarPolls.controllers.TechnologyController;
import ru.semavin.TechRadarPolls.dtos.PollDTO;
import ru.semavin.TechRadarPolls.dtos.TechnologyDTO;
import ru.semavin.TechRadarPolls.dtos.TechnologyWithPollsResultDTO;
import ru.semavin.TechRadarPolls.models.*;
import ru.semavin.TechRadarPolls.services.*;


import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(TechnologyController.class)
class TechnologyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TechnologyService technologyService;

    @MockBean
    private PollService pollService;

    @MockBean
    private RingService ringService;

    @MockBean
    private UserService userService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private SectionService sectionService;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ModelMapper modelMapper;

    private Technology technology;
    private Category category;
    private Section section;
    private Ring ring;
    private PollDTO pollDTO;

    @BeforeEach
    void setup() {
        category =Category.builder().cat_id(1L).catName("BACKEND").build();
        section = Section.builder().sec_id(2L).secName("TOOLS").build();
        ring = Ring.builder().ring_id(1L).ringName("ADOPT").build();
        technology = Technology.builder().techId(1L).category(category)
                .section(section).ring(ring).name("Spring Boot").description("Framework for building Java applications.").build();

        pollDTO = PollDTO.builder()
                .user_id(1)
                .tech_id(1)
                .ringResult("ADOPT")
                .build();
    }

    @Test
    void testFindAllByFiltersSuccess() throws Exception {
        List<Technology> technologies = List.of(technology);
        List<TechnologyDTO> technologyDTOs = List.of(
                TechnologyDTO.builder()
                        .id(1L)
                        .name("Java")
                        .description("A programming language")
                        .category("Software")
                        .ring("ADOPT")
                        .build()
        );

        when(technologyService.findAllByFilter(any(), any())).thenReturn(technologies);
        when(categoryService.findByName("Software")).thenReturn(Optional.of(category));
        when(sectionService.findByName("Backend")).thenReturn(Optional.of(section));

        mockMvc.perform(get("/api/technology")
                        .param("category", "Software")
                        .param("section", "Backend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technologies[0].name").value("Spring Boot"))
                .andExpect(jsonPath("$.technologies[0].category").value("BACKEND"))
                .andExpect(jsonPath("$.technologies[0].ring").value("ADOPT"));
    }

    @Test
    void testGetVotesCountForAllRingsSuccess() throws Exception {
        Map<String, Integer> votes = Map.of("ADOPT", 10, "TRIAL", 5);
        TechnologyWithPollsResultDTO expectedResponse = TechnologyWithPollsResultDTO.builder()
                .techId(1L)
                .name("Java")
                .category("Software")
                .section("Backend")
                .votes(votes)
                .build();

        when(technologyService.findOne(1)).thenReturn(Optional.of(technology));
        when(pollService.countUsersForTechByAllRings(1)).thenReturn(votes);

        mockMvc.perform(get("/api/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.techId").value(1))
                .andExpect(jsonPath("$.name").value("Spring Boot"))
                .andExpect(jsonPath("$.votes.ADOPT").value(10))
                .andExpect(jsonPath("$.votes.TRIAL").value(5));
    }

    @Test
    void testSendPollSuccess() throws Exception {
        when(ringService.findByName("ADOPT")).thenReturn(Optional.of(Ring.builder().ringName("ADOPT").build()));
        when(userService.findById(1)).thenReturn(Optional.of(new User()));
        when(technologyService.findOne(1)).thenReturn(Optional.of(technology));

        mockMvc.perform(post("/poll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pollDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Результат опроса успешно добавлен"));
    }

    @Test
    void testSendPollBadRequest() throws Exception {
        pollDTO.setRingResult(null);  // Invalid data to trigger BAD REQUEST

        mockMvc.perform(post("/poll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pollDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("BAD REQUEST"));
    }
}
