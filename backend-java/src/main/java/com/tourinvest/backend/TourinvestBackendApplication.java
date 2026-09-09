package com.tourinvest.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque de la API REST Spring Boot del backend Java.
 *
 * NOTA: Este archivo fue corregido como parte del todo (#A1/A2). El código fuente
 * de la clase se encontraba erróneamente dentro de "src/main/resources/application.properties"
 * (texto plano), por lo que Spring Boot no podía arrancar ni resolver los placeholders
 * @Value("${jwt.secret}") / @Value("${jwt.expiration-ms}") de JwtUtil.
 */
@SpringBootApplication
public class TourinvestBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourinvestBackendApplication.class, args);
    }
}
