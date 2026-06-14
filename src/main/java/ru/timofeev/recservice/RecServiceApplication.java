package ru.timofeev.recservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class RecServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecServiceApplication.class, args);
    }

}
