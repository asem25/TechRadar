package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.models.Category;
import ru.semavin.TechRadarPolls.repositories.CategoryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public Optional<Category> findByName(String category){
        return categoryRepository.findByCatNameIgnoreCase(category);
    }
}
