package com.tourinvest.backend.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Paso 6 de la guía — Conexión JDBC pura para el módulo DAO.
 *
 * Apunta a la MISMA base de datos 'tourinvest' que usa el backend Spring Boot
 * (ver src/main/resources/application.properties), de modo que los usuarios
 * creados aquí sean los mismos que valida /auth/login y que lista
 * /admin/usuarios.
 *
 * Si cambian las credenciales de MySQL, actualízalas también en
 * application.properties.
 */
public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/tourinvest?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "Ju4nd1eg0fuentes*";

    private DatabaseConnection() {
        // Uso exclusivo vía getConnection(): no se instancia.
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
