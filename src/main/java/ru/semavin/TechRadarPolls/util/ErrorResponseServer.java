package ru.semavin.TechRadarPolls.util;

public class ErrorResponseServer extends Exception{
    public ErrorResponseServer(String message) {
        super(message);
    }
}
