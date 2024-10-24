package ru.semavin.TechRadarPolls.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.repositories.UserRepository;
import ru.semavin.TechRadarPolls.models.User;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public Optional<User> findById(Integer id){
        return userRepository.findById(id);
    }
}
