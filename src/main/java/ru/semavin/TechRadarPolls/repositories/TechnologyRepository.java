package ru.semavin.TechRadarPolls.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.semavin.TechRadarPolls.models.Category;
import ru.semavin.TechRadarPolls.models.Section;
import ru.semavin.TechRadarPolls.models.Technology;


import java.util.List;
@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {

    List<Technology> findByCategoryAndSection(Category category,Section section);
    List<Technology> findByCategory(Category category);
    List<Technology> findBySection(Section section);
}
