package ru.semavin.TechRadarPolls.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.dtos.AuthResponse;
import ru.semavin.TechRadarPolls.dtos.KafkaResponse;
import ru.semavin.TechRadarPolls.dtos.RefreshRequest;
import ru.semavin.TechRadarPolls.util.JsonUnknownFormatException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TechRadarKafkaListener {
    private final ConcurrentHashMap<String, CompletableFuture<String>> responseMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    @Autowired
    public TechRadarKafkaListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<String> registerResponseFuture(String key) {
        CompletableFuture<String> future = new CompletableFuture<>();
        responseMap.put(key, future);
        return future;
    }
    @KafkaListener(topics = {"user.register.response"})
    public void handleRegisterResponse(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonMessage = record.value();
        try {
            if(isValidJson(jsonMessage, KafkaResponse.class)) {
                KafkaResponse response = objectMapper.readValue(jsonMessage, KafkaResponse.class);
                log.info("Received response for key {}: {}", key, response);

                CompletableFuture<String> future = responseMap.remove(key);
                if (future != null) {
                    future.complete(response.toString());
                } else {
                    log.warn("No CompletableFuture found for key: {}", key);
                }
            }else {
                log.error("Unknown message format for key {}: {}", key, jsonMessage);
                CompletableFuture<String> future = responseMap.remove(key);
                if (future != null) {
                    future.completeExceptionally(new JsonUnknownFormatException("Unknown message format"));
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing Kafka message: {}", record.value(), e);
        }
    }
    @KafkaListener(topics = {"user.login.response"})
    public void handleLoginResponse(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonMessage = record.value();

        try {
            if (isValidJson(jsonMessage, AuthResponse.class)) {
                handleAuthResponse(key, jsonMessage);
            }
            else if (isValidJson(jsonMessage, KafkaResponse.class)) {
                handleKafkaResponse(key, jsonMessage);
            } else {
                log.error("Unknown message format for key {}: {}", key, jsonMessage);
                CompletableFuture<String> future = responseMap.remove(key);
                if (future != null) {
                    future.completeExceptionally(new JsonUnknownFormatException("Unknown message format"));
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing Kafka message: {}", record.value(), e);
            CompletableFuture<String> future = responseMap.remove(key);
            if (future != null) {
                future.completeExceptionally(e);
            }
        }
    }
    @KafkaListener(topics = "user.token.response")
    public void handleRefreshTokenResponse(ConsumerRecord<String, String> record){
        String key = record.key();
        String jsonMessage = record.value();
        try{
            if (isValidJson(jsonMessage, AuthResponse.class)){
                handleAuthResponse(key, jsonMessage);
            }else if(isValidJson(jsonMessage, KafkaResponse.class)){
               handleKafkaResponse(key, jsonMessage);
            }
        }catch (JsonProcessingException e){
            log.error("Error deserializing Kafka message: {}", record.value(), e);
            CompletableFuture<String> future = responseMap.remove(key);
            if (future != null) {
                future.completeExceptionally(e);
            }
        }
    }

    private void handleAuthResponse(String key, String jsonMessage) throws JsonProcessingException {
        AuthResponse authResponse = objectMapper.readValue(jsonMessage, AuthResponse.class);
        log.info("Received AuthResponse for key {}: {}", key, authResponse);

        CompletableFuture<String> future = responseMap.remove(key);
        if (future != null) {
            future.complete(authResponse.toString());
        } else {
            log.warn("No CompletableFuture found for key: {}", key);
        }
    }
    private void handleKafkaResponse(String key, String jsonMessage) throws JsonProcessingException {
        KafkaResponse response = objectMapper.readValue(jsonMessage, KafkaResponse.class);
        log.info("Received KafkaResponse for key {}: {}", key, response);

        CompletableFuture<String> future = responseMap.remove(key);
        if (future != null) {
            future.complete(response.toString());
        } else {
            log.warn("No CompletableFuture found for key: {}", key);
        }
    }

    private boolean isValidJson(String json, Class<?> clazz) {
        try {
            objectMapper.readValue(json, clazz);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
