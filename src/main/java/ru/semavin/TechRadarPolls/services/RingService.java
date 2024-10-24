package ru.semavin.TechRadarPolls.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.models.Ring;
import ru.semavin.TechRadarPolls.repositories.RingRepository;
import java.util.*;
@Service
@RequiredArgsConstructor
public class RingService {
    private final RingRepository ringRepository;
    public List<Ring> findAll(){
        return ringRepository.findAll();
    }
}
