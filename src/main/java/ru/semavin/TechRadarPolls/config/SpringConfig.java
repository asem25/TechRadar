package ru.semavin.TechRadarPolls.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;



@Configuration
@RequiredArgsConstructor

public class SpringConfig {


        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
        @Bean
        public ModelMapper modelMapper(){return new ModelMapper();}
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .info(new Info()
                                .title("Tech Radar Polls API")
                                .version("1.0")
                                .description("API для управления Tech Radar Polls")
                                .contact(new Contact()
                                        .name("Semavin Aleksandr")
                                        .email("asemavin250604@gmail.com"))
                                .license(new License()
                                        .name("Apache 2.0")
                                        .url("https://springdoc.org")));
        }
}
