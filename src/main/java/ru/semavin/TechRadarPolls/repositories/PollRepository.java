package ru.semavin.TechRadarPolls.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.semavin.TechRadarPolls.models.Poll;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    List<Poll> findByTechnologyTechId(Long technology_techId);
    void deleteByTechnologyTechId(Long technology_techId);


}
