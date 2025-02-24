package ru.semavin.TechRadarPolls.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semavin.TechRadarPolls.repositories.UserRepository;
import ru.semavin.TechRadarPolls.models.User;
import ru.semavin.TechRadarPolls.util.BaseNotFoundException;
import ru.semavin.TechRadarPolls.util.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> BaseNotFoundException.create(User.class));
    }
}
