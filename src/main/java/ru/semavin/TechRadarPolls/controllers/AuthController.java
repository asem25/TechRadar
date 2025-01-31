package ru.semavin.TechRadarPolls.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;
import ru.semavin.TechRadarPolls.dtos.AuthResponse;
import ru.semavin.TechRadarPolls.dtos.RefreshRequest;
import ru.semavin.TechRadarPolls.dtos.UserLoginDTO;
import ru.semavin.TechRadarPolls.dtos.UserRegisterDTO;
import ru.semavin.TechRadarPolls.listener.TechRadarKafkaListener;
import ru.semavin.TechRadarPolls.producer.TechRadarKafkaProducer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TechRadarKafkaProducer techRadarKafkaProducer;
    private final TechRadarKafkaListener techRadarKafkaListener;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) {
        try {

            CompletableFuture<String> futureResponse = techRadarKafkaListener.registerResponseFuture("login");
            techRadarKafkaProducer.sendLoginEvent("login", userLoginDTO);

            String response = futureResponse.get(10, TimeUnit.SECONDS);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to login user");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            CompletableFuture<String> futureResponse = techRadarKafkaListener.registerResponseFuture("register");
            techRadarKafkaProducer.sendRegisterEvent("register", userRegisterDTO);

            String response = futureResponse.get(10, TimeUnit.SECONDS);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to register user");
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody RefreshRequest refreshRequest){
        try {
            CompletableFuture<String> futureResponse = techRadarKafkaListener.registerResponseFuture("refresh");
            techRadarKafkaProducer.sendRefreshEvent("refresh", refreshRequest);

            String response = futureResponse.get(10, TimeUnit.SECONDS);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to refresh token user");
        }
    }
}
