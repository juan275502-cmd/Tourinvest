package com.tourinvest.backend.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Empresa, implementado con JDBC puro.
 * inserción, consulta, actualización y eliminación mediante JDBC.
 */
public class EmpresaJdbcDAO {

    // ---------- CREATE ----------
    public int insertarEmpresa(String nombre, String sector, String pais, String simbolo) throws SQLException {
        String sql = "INSERT INTO empresas (nombre, sector, pais, simbolo) VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionJDBC.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, nombre);
            statement.setString(2, sector);
            statement.setString(3, pais);
            statement.setString(4, simbolo);
            statement.executeUpdate();

            try (ResultSet clavesGeneradas = statement.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    return clavesGeneradas.getInt(1);
                }
            }
        }
        return -1;
    }

    // ---------- READ ----------
    public List<String> listarEmpresas() throws SQLException {
        String sql = "SELECT id_empresa, nombre, sector, pais, simbolo FROM empresas ORDER BY nombre";
        List<String> empresas = new ArrayList<>();

        try (Connection conexion = ConexionJDBC.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                empresas.add(String.format("%d | %s | %s | %s | %s",
                        resultado.getInt("id_empresa"),
                        resultado.getString("nombre"),
                        resultado.getString("sector"),
                        resultado.getString("pais"),
                        resultado.getString("simbolo")));
            }
        }
        return empresas;
    }

    // ---------- UPDATE ----------
    public boolean actualizarEmpresa(int idEmpresa, String nombre, String sector, String pais) throws SQLException {
        String sql = "UPDATE empresas SET nombre = ?, sector = ?, pais = ? WHERE id_empresa = ?";

        try (Connection conexion = ConexionJDBC.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, nombre);
            statement.setString(2, sector);
            statement.setString(3, pais);
            statement.setInt(4, idEmpresa);

            return statement.executeUpdate() > 0;
        }
    }

    // ---------- DELETE ----------
    public boolean eliminarEmpresa(int idEmpresa) throws SQLException {
        String sql = "DELETE FROM empresas WHERE id_empresa = ?";

        try (Connection conexion = ConexionJDBC.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idEmpresa);
            return statement.executeUpdate() > 0;
        }
    }
    // ---------- READ (individual) ----------
    public String buscarEmpresaPorId(int idEmpresa) throws SQLException {
        String sql = "SELECT id_empresa, nombre, sector, pais, simbolo FROM empresas WHERE id_empresa = ?";

        try (Connection conexion = ConexionJDBC.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idEmpresa);

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return String.format("%d | %s | %s | %s | %s",
                            resultado.getInt("id_empresa"),
                            resultado.getString("nombre"),
                            resultado.getString("sector"),
                            resultado.getString("pais"),
                            resultado.getString("simbolo"));
                }
            }
        }
        return null; // no encontrada
    }
}