package com.kiranastore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Kirana2Application {

    public static void main(String[] args) {
        SpringApplication.run(Kirana2Application.class, args);
    }

}
