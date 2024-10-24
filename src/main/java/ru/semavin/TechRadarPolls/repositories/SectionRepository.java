package ru.semavin.TechRadarPolls.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.semavin.TechRadarPolls.models.Section;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    Optional<Section> findBySecNameIgnoreCase(String section);
}
