package ru.semavin.TechRadarPolls.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor

public class SpringConfig {


        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
        @Bean
        public ModelMapper modelMapper(){return new ModelMapper();}
}
