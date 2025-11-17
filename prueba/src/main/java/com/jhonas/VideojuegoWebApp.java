package com.jhonas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideojuegoWebApp {
    
    public static void main(String[] args) {
        SpringApplication.run(VideojuegoWebApp.class, args);
        System.out.println("🎮 Aplicación Web iniciada en: http://localhost:8080");
    }
}
