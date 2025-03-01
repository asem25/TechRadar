package ru.semavin.TechRadarPolls.controllers;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.semavin.TechRadarPolls.util.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(basePackages = "ru.semavin.TechRadarPolls.controllers")
public class GlobalControllerAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException userNotFoundException){
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(userNotFoundException.getMessage());
    }
    @ExceptionHandler(TechnologyNotFoundException.class)
    public ResponseEntity<String> handleTechnologyNotFoundException(TechnologyNotFoundException technologyNotFoundException){
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(technologyNotFoundException.getMessage());
    }

    @ExceptionHandler(RingNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(RingNotFoundException ringNotFoundException){
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(ringNotFoundException.getMessage());
    }
    @ExceptionHandler(ErrorResponseServer.class)
    public ResponseEntity<String> handleServerError(ErrorResponseServer errorResponseServer){
        return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(errorResponseServer.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
}
