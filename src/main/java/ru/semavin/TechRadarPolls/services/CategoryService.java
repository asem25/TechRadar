package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.models.Category;
import ru.semavin.TechRadarPolls.repositories.CategoryRepository;
import ru.semavin.TechRadarPolls.util.BaseNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public Category findByName(String category){
        return categoryRepository.findByCatNameIgnoreCase(category).orElseThrow(() -> BaseNotFoundException.create(Category.class));

    }
    public Category findByNameWithListExceptions(String name, List<String> exceptinMessageList){
        return categoryRepository.findByCatNameIgnoreCase(name).orElseGet(
                () ->
                {
                    exceptinMessageList.add(BaseNotFoundException.create(Category.class).getMessage());
                    return null;
                }
        );
    }
}
