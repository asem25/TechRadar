package ru.semavin.TechRadarPolls;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import ru.semavin.TechRadarPolls.controllers.TechnologyController;
import ru.semavin.TechRadarPolls.dtos.PollDTO;
import ru.semavin.TechRadarPolls.dtos.TechnologyWithPollsResultDTO;
import ru.semavin.TechRadarPolls.models.*;
import ru.semavin.TechRadarPolls.services.*;
import ru.semavin.TechRadarPolls.util.ErrorResponseServer;
import ru.semavin.TechRadarPolls.util.MessageResponsePoll;
import ru.semavin.TechRadarPolls.util.SectionNotFoundException;
import ru.semavin.TechRadarPolls.util.CategoryNotFoundException;
import ru.semavin.TechRadarPolls.util.TechnologyNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class TechnologyControllerTest {

    @InjectMocks
    private TechnologyController technologyController;

    @Mock
    private TechnologyService technologyService;
    @Mock
    private PollService pollService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private RingService ringService;
    @Mock
    private UserService userService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private SectionService sectionService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testFindAllByFilters_WithValidParameters_ShouldReturnTechnologies() {
        // Arrange
        String categoryName = "Category1";
        String sectionName = "Section1";
        String ringName = "Adopt"; // Пример имени кольца
        Category category = Category.builder().cat_id(1L).catName(categoryName).build();
        Section section = Section.builder().sec_id(1L).secName(sectionName).build();
        Ring ring = Ring.builder().ring_id(1L).ringName(ringName).build(); // Создание объекта Ring
        Technology technology = Technology.builder()
                .techId(1L)
                .name("Tech1")
                .description("Description")
                .category(category)
                .section(section)
                .ring(ring)
                .build();
        when(categoryService.findByName(categoryName)).thenReturn(Optional.of(category));
        when(sectionService.findByName(sectionName)).thenReturn(Optional.of(section));
        when(technologyService.findAllByFilter(category, section)).thenReturn(List.of(technology));

        // Act
        ResponseEntity<Map<String, Object>> response = technologyController.findAllByFilters(categoryName, sectionName);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("technologies"));
        assertEquals(1, ((List<?>) body.get("technologies")).size());
    }

    @Test
    void testFindAllByFilters_WithInvalidCategory_ShouldReturnBadRequest() {
        // Arrange
        String categoryName = "InvalidCategory";
        String sectionName = "Section1";
        when(categoryService.findByName(categoryName)).thenReturn(Optional.empty());
        when(sectionService.findByName(sectionName)).thenReturn(Optional.of(new Section(1L, sectionName)));

        // Act
        ResponseEntity<Map<String, Object>> response = technologyController.findAllByFilters(categoryName, sectionName);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid query parameters", body.get("error"));
        Map<String, String> details = (Map<String, String>) body.get("details");
        assertTrue(details.containsKey("category"));
        assertEquals("CATEGORY NOT FOUND", details.get("category"));
    }

    @Test
    void testFindAllByFilters_WithInvalidSection_ShouldReturnBadRequest() {
        // Arrange
        String categoryName = "Category1";
        String sectionName = "InvalidSection";
        when(categoryService.findByName(categoryName)).thenReturn(Optional.of(new Category(1L, categoryName)));
        when(sectionService.findByName(sectionName)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = technologyController.findAllByFilters(categoryName, sectionName);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid query parameters", body.get("error"));
        Map<String, String> details = (Map<String, String>) body.get("details");
        assertTrue(details.containsKey("section"));
        assertEquals("SECTION NOT FOUND", details.get("section"));
    }

    @Test
    void testGetVotesCountForAllRings_ValidTechnologyId_ReturnsTechnologyWithVotes() throws ErrorResponseServer {
        // Arrange
        Technology technology = Technology.builder().techId(1L).build();
        when(technologyService.findOne(anyInt())).thenReturn(Optional.of(technology));
        Ring ring1 = new Ring();
        ring1.setRingName("Adopt");

        Ring ring2 = new Ring();
        ring2.setRingName("Trial");

        // Список голосов
        Poll poll1 = new Poll();
        poll1.setRing(ring1);
        poll1.setUser(User.builder().userId(1L).build());

        Poll poll2 = new Poll();
        poll2.setRing(ring1);
        poll2.setUser(User.builder().userId(2L).build());

        Poll poll3 = new Poll();
        poll3.setRing(ring2);
        poll3.setUser(User.builder().userId(3L).build()); // Пользователь 1 повторно голосует, но он считается уникальным

        List<Poll> polls = List.of(poll1, poll2, poll3);



        when(pollService.countUsersForTechByAllRings(1)).thenReturn(
                Map.of(
                        ring1, 2,
                        ring2, 1
                )
        );
        when(modelMapper.map(any(), eq(TechnologyWithPollsResultDTO.class))).thenReturn(new TechnologyWithPollsResultDTO());

        // Act
        TechnologyWithPollsResultDTO resultDTO = technologyController.getVotesCountForAllRings(1);

        // Assert
        assertNotNull(resultDTO);
        verify(technologyService).findOne(1);
    }

    @Test
    void testGetVotesCountForAllRings_InvalidTechnologyId_ThrowsException() {
        // Arrange
        when(technologyService.findOne(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TechnologyNotFoundException.class, () -> technologyController.getVotesCountForAllRings(1));
    }

    @Test
    void testSendPoll_ValidPollDTO_ShouldReturnSuccessMessage() {
        // Arrange
        PollDTO pollDTO = new PollDTO();
        pollDTO.setRingResult("Adopt");
        pollDTO.setUser_id(1);
        pollDTO.setTech_id(1);
        when(ringService.findByName(pollDTO.getRingResult())).thenReturn(Optional.of(new Ring()));
        when(userService.findById(pollDTO.getUser_id())).thenReturn(Optional.of(new User()));
        when(technologyService.findOne(pollDTO.getTech_id())).thenReturn(Optional.of(new Technology()));

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        MessageResponsePoll response = technologyController.sendPoll(pollDTO, bindingResult);

        // Assert
        assertEquals("Результат опроса успешно добавлен", response.getMessage());
    }

    @Test
    void testSendPoll_InvalidPollDTO_ShouldReturnBadRequestMessage() {
        // Arrange
        PollDTO pollDTO = new PollDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        MessageResponsePoll response = technologyController.sendPoll(pollDTO, bindingResult);

        // Assert
        assertEquals("BAD REQUEST", response.getMessage());
    }

    @Test
    void testSendPoll_ExceptionThrown_ShouldReturnInternalServerErrorMessage() {
        // Arrange
        PollDTO pollDTO = new PollDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(ringService.findByName(any())).thenThrow(new RuntimeException("Error"));

        // Act
        MessageResponsePoll response = technologyController.sendPoll(pollDTO, bindingResult);

        // Assert
        assertEquals("500", response.getCode());
    }
}
