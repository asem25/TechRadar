package ru.semavin.TechRadarPolls.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.dtos.AuthResponse;
import ru.semavin.TechRadarPolls.util.JsonResponseFieldsException;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TechRadarKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper ob;
    private static final String TOPIC_REGISTER = "user.register.request";
    private static final String TOPIC_LOGIN = "user.login.request";
    private static final String TOPIC_REFRESH = "user.token.request";
    private static final String TOPIC_LOGOUT = "user.logout.request";
    private static final String TOPIC_VALIDATE = "user.validate.request";

    public TechRadarKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper ob) {
        this.kafkaTemplate = kafkaTemplate;
        this.ob = ob;
    }

    public void sendRegisterEvent(String key, Object message) {
        sendEvent(key, message, TOPIC_REGISTER);
    }

    public void sendLoginEvent(String key, Object message) {
        sendEvent(key, message, TOPIC_LOGIN);
    }
    public void sendRefreshEvent(String key, Object message){
        sendEvent(key, message, TOPIC_REFRESH);
    }
    public void sendLogoutEvent(String key, Object message) {
        sendEvent(key, message, TOPIC_LOGOUT);
    }
    public void sendValidateEvent(String key, Object message){
        sendEvent(key, message, TOPIC_VALIDATE);
    }
    private void sendEvent(String key, Object message, String topicName) {
        try {
            String jsonMessage = ob.writeValueAsString(message);
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, key, jsonMessage);

            future.thenAccept(result ->
                    log.info("Message sent successfully to topic {} with key {}: {}", topicName, key, message)
            ).exceptionally(ex -> {
                log.error("Failed to send message to topic {} with key {}: {}", topicName, key, ex.getMessage());
                return null;
            });
        } catch (JsonProcessingException e) {
            throw new JsonResponseFieldsException("Error in request json");
        }
    }



}
