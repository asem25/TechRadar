package ru.semavin.TechRadarPolls.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.semavin.TechRadarPolls.models.Poll;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    // Получение всех опросов по идентификатору технологии
    List<Poll> findByTechnologyTechId(Long technology_techId);

    // Получение всех опросов по идентификатору технологии и времени опроса
    List<Poll> findByTechnologyTechIdAndTimeAfter(Long technologyId, LocalDateTime pollTime);

    // Получение последнего опроса по идентификатору технологии после указанного времени
    Poll findFirstByTechnologyTechIdAndTimeAfterOrderByTimeDesc(Long technologyId, LocalDateTime pollTime);

    // Получение последнего опроса по идентификатору технологии (без времени)
    Poll findFirstByTechnologyTechIdOrderByTimeDesc(Long technologyId);

    // Получение двух последних опросов по идентификатору технологии
    List<Poll> findTop2ByTechnologyTechIdOrderByTimeDesc(Long technologyId);

}
