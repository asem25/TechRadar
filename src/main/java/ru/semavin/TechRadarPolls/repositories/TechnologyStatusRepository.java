package ru.semavin.TechRadarPolls.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.semavin.TechRadarPolls.models.TechnologyStatus;

import java.util.Optional;

public interface TechnologyStatusRepository extends JpaRepository<TechnologyStatus, Integer> {
    Optional<TechnologyStatus> findByStatusIgnoreCase(String stat_name);
}
