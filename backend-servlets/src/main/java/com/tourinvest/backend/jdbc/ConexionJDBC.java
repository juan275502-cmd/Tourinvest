package com.tourinvest.backend.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilidad de conexión JDBC pura, independiente de Spring Data JPA.
 * Demuestra conexión directa a MySQL
 * mediante java.sql, sin pasar por el ORM (Hibernate) usado en el resto
 * del backend.
 */
public class ConexionJDBC {

    private static final String URL = "jdbc:mysql://localhost:3306/tourinvest?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}