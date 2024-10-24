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

//    @Query("SELECT t FROM Technology t WHERE (:category IS NULL OR t.category.cat_id = :category) AND (:section IS NULL OR t.section = :section)")
//    List<Technology> findByCategoryOrSection(@Param("category") Category category, @Param("section") Section section);
}
