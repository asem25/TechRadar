package ru.semavin.TechRadarPolls.util;

public class BaseNotFoundException extends RuntimeException{
    public BaseNotFoundException(String message) {
        super(message);
    }
    public static RuntimeException create(Class<?> clazzException){
        return switch (clazzException.getSimpleName()){
            case "Section" -> new SectionNotFoundException("Section not found");
            case "Category" -> new CategoryNotFoundException("Category not found");
            case "Technology" -> new TechnologyNotFoundException("Technology not found");
            case "User" -> new UserNotFoundException("User not found");
            case "Ring" -> new RingNotFoundException("Ring not found");
            default -> new RuntimeException(clazzException.getSimpleName() + " not found");
        };
    }
}
