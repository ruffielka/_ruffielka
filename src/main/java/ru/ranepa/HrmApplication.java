package ru.ranepa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrmApplication.class, args);

        // Сообщение для пользователя в консоли после успешного старта
        System.out.println(" HRM Lite API запущен!");
        System.out.println(" Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println(" H2 Console: http://localhost:8080/h2-console");
    }
}