package ru.semavin.TechRadarPolls.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.semavin.TechRadarPolls.config.TestConfig;
import ru.semavin.TechRadarPolls.dtos.RefreshRequest;
import ru.semavin.TechRadarPolls.dtos.UserLoginDTO;
import ru.semavin.TechRadarPolls.dtos.UserRegisterDTO;
import ru.semavin.TechRadarPolls.producer.TechRadarKafkaProducer;
import ru.semavin.TechRadarPolls.listener.TechRadarKafkaListener;

import java.util.concurrent.CompletableFuture;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TechRadarKafkaProducer techRadarKafkaProducer;

    @MockBean
    private TechRadarKafkaListener techRadarKafkaListener;

    @Test
    void testLoginSuccess() throws Exception {
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("testuser@maii.ru")
                .password("secret")
                .build();

        CompletableFuture<String> futureResponse = CompletableFuture.completedFuture("Login Successful");
        given(techRadarKafkaListener.registerResponseFuture("login")).willReturn(futureResponse);


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login Successful"));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        UserRegisterDTO registerDTO = UserRegisterDTO.builder()
                .email("testuser@maii.ru")
                .password("newpassword")
                .email("newuser@example.com")
                .build();

        CompletableFuture<String> futureResponse = CompletableFuture.completedFuture("Registration Successful");
        given(techRadarKafkaListener.registerResponseFuture("register")).willReturn(futureResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration Successful"));
    }

    @Test
    void testRefreshSuccess() throws Exception {
        RefreshRequest refreshRequest = RefreshRequest.builder()
                .refreshToken("some-valid-token")
                .build();

        CompletableFuture<String> futureResponse = CompletableFuture.completedFuture("Refresh Successful");
        given(techRadarKafkaListener.registerResponseFuture("refresh")).willReturn(futureResponse);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Refresh Successful"));
    }

    @Test
    void testLoginFailure() throws Exception {
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("testuser@maii.ru")
                .password("secret")
                .build();

        CompletableFuture<String> futureResponse = new CompletableFuture<>();
        futureResponse.completeExceptionally(new RuntimeException("Kafka error"));
        given(techRadarKafkaListener.registerResponseFuture("login")).willReturn(futureResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to login user"));
    }
}
