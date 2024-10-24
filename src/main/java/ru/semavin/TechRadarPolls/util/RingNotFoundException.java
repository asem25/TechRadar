package ru.semavin.TechRadarPolls.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RingNotFoundException extends RuntimeException {
    public RingNotFoundException(String message) {
        super(message);
    }
}
