package com.itterior.itterior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ItTeriorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItTeriorApplication.class, args);
    }

}
