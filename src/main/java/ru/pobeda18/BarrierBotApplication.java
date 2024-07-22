package ru.pobeda18;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BarrierBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BarrierBotApplication.class, args);
    }
}
