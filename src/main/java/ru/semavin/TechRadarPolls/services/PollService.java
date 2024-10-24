package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.models.Ring;
import ru.semavin.TechRadarPolls.models.Poll;
import ru.semavin.TechRadarPolls.repositories.PollRepository;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;
    private final RingService ringService;

    public Map<Ring, Integer> countUsersForTechByAllRings(Integer techId){
        List<Poll> polls = pollRepository.findByTechnologyTechId(Long.valueOf(techId));

        Map<Ring, Set<Long>> ringUserMap = polls.stream()
                .collect(Collectors.groupingBy(Poll::getRing,
                        Collectors.mapping(poll -> poll.getUser().getUserId(),
                                Collectors.toSet())));

        Map<Ring, Integer> ringCountMap = new HashMap<>();
        for (Ring ring : ringService.findAll()) {
            ringCountMap.put(ring, ringUserMap.getOrDefault(ring, Set.of()).size());
        }

        return ringCountMap;

    }
    public void save(Poll poll){
        pollRepository.save(poll);
    }
}
