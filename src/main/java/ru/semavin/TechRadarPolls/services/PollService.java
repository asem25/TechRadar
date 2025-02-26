package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.semavin.TechRadarPolls.models.Ring;
import ru.semavin.TechRadarPolls.models.Poll;
import ru.semavin.TechRadarPolls.repositories.PollRepository;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;
    private final RingService ringService;

    public Map<String, Integer> countUsersForTechByAllRings(Integer techId){
        List<Poll> polls = pollRepository.findByTechnologyTechId(Long.valueOf(techId));

        Map<Ring, Set<Long>> ringUserMap = polls.stream()
                .collect(Collectors.groupingBy(Poll::getRing,
                        Collectors.mapping(poll -> poll.getUser().getUserId(),
                                Collectors.toSet())));

        Map<String, Integer> ringCountMap = new TreeMap<>();
        for (Ring ring : ringService.findAll()) {
            ringCountMap.put(ring.getRingName(), ringUserMap.getOrDefault(ring, Set.of()).size());
        }
        return ringCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

    }

    @Transactional
    public void save(Poll poll){
        pollRepository.save(poll);
    }
    @Transactional
    public void deleteByTechnology(Integer id){
        pollRepository.deleteByTechnologyTechId(Long.valueOf(id));
    }
}
