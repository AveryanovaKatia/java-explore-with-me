package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableFeignClients
public class EWMMainServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(EWMMainServiceApp.class, args);

        log.info("Приложение EWMMainServiceApp запущено");
    }
}
