package ru.semavin.TechRadarPolls.config;

import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.semavin.TechRadarPolls.listener.TechRadarKafkaListener;
import ru.semavin.TechRadarPolls.producer.TechRadarKafkaProducer;
import ru.semavin.TechRadarPolls.repositories.TechnologyRepository;
import ru.semavin.TechRadarPolls.security.service.CustomUserDetailsService;
import ru.semavin.TechRadarPolls.security.service.JwtTokenProvider;
import ru.semavin.TechRadarPolls.services.*;

@TestConfiguration
public class TestConfig {
    @Bean
    public TechRadarKafkaProducer techRadarKafkaProducer(){ return Mockito.mock(TechRadarKafkaProducer.class);}

    @Bean
    public TechRadarKafkaListener techRadarKafkaListener(){ return Mockito.mock(TechRadarKafkaListener.class);}
    @Bean
    public JwtTokenProvider jwtTokenProvider(){
        return Mockito.mock(JwtTokenProvider.class);
    }
    @Bean
    public TechnologyService technologyService() {
        return Mockito.mock(TechnologyService.class);
    }

    @Bean
    public PollService pollService() {
        return Mockito.mock(PollService.class);
    }

    @Bean
    public RingService ringService() {
        return Mockito.mock(RingService.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public CategoryService categoryService() {
        return Mockito.mock(CategoryService.class);
    }

    @Bean
    public SectionService sectionService() {
        return Mockito.mock(SectionService.class);
    }

    @Bean
    public ModelMapper modelMapper() {
        return Mockito.mock(ModelMapper.class);
    }
    @Bean
    public TechnologyRepository technologyRepository()
    {
        return Mockito.mock(TechnologyRepository.class);
    }
    @Bean
    public CustomUserDetailsService customUserDetailsService(){
        return Mockito.mock(CustomUserDetailsService.class);
    }
}
