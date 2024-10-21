package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class EWMStatServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(EWMStatServiceApp.class, args);

        log.info("Приложение EWMStatServiceApp запущено");
    }
}
