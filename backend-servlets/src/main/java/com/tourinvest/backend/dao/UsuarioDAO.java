package com.tourinvest.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * PASO 6 DE LA GUÍA — DAO (Data Access Object) con JDBC puro.
 *
 * CRUD de la tabla `usuarios` usando PreparedStatement (buena práctica que
 * evita construir SQL concatenando los datos introducidos: sin inyección
 * SQL). Trabaja sobre la MISMA base `tourinvest` del proyecto (query.sql),
 * así que lo que crea/actualiza aquí es lo mismo que valida el login de
 * Spring Security (/auth/login) y que lista el panel /admin/usuarios.
 *
 * Alineación con el esquema real:
 *  · La columna del nombre es `nombre1` (la guía la llama `nombre`).
 *  · `estado` es ENUM('Activo','Inactivo'): el POJO lo expone como boolean.
 *  · La contraseña se guarda con BCrypt, igual que hace AuthService, para
 *    que los usuarios creados por el DAO puedan iniciar sesión.
 */
public class UsuarioDAO {

    private static final BCryptPasswordEncoder CODIFICADOR = new BCryptPasswordEncoder();

    private static final String COLUMNAS = """
            u.id_usuario, u.nombre1, u.apellido1, u.cedula, u.fecha_nacimiento,
            u.correo, u.estado, u.id_rol, r.nombre AS rol_nombre
            """;

    // ---------- CREATE ----------

    public boolean crearUsuario(Usuario usuario) {
        String sql = """
                INSERT INTO usuarios
                (nombre1, apellido1, cedula, fecha_nacimiento, correo, contrasena, estado, id_rol)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getApellido());
            statement.setString(3, usuario.getCedula());
            statement.setString(4, usuario.getFechaNacimiento());
            statement.setString(5, usuario.getCorreo());
            statement.setString(6, CODIFICADOR.encode(usuario.getContrasena()));
            statement.setString(7, usuario.isEstado() ? "Activo" : "Inactivo");
            statement.setInt(8, usuario.getIdRol());

            statement.executeUpdate();
            return true;

        } catch (SQLException exception) {
            System.out.println("Error al crear usuario: " + exception.getMessage());
            return false;
        }
    }

    // ---------- READ ----------

    public List<Usuario> listarUsuarios() {
        String sql = """
                SELECT %s
                FROM usuarios u
                INNER JOIN roles r ON u.id_rol = r.id_rol
                ORDER BY u.id_usuario
                """.formatted(COLUMNAS);

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                usuarios.add(mapearFila(resultado));
            }

        } catch (SQLException exception) {
            System.out.println("Error al listar usuarios: " + exception.getMessage());
        }

        return usuarios;
    }

    public Usuario buscarPorId(int idUsuario) {
        String sql = """
                SELECT %s
                FROM usuarios u
                INNER JOIN roles r ON u.id_rol = r.id_rol
                WHERE u.id_usuario = ?
                """.formatted(COLUMNAS);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idUsuario);

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return mapearFila(resultado);
                }
            }

        } catch (SQLException exception) {
            System.out.println("Error al buscar usuario: " + exception.getMessage());
        }

        return null;
    }

    public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, correo);

            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }

        } catch (SQLException exception) {
            System.out.println("Error al verificar correo: " + exception.getMessage());
            return false;
        }
    }

    /** Traduce una fila del ResultSet a un objeto Usuario. */
    private Usuario mapearFila(ResultSet resultado) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultado.getInt("id_usuario"));
        usuario.setNombre(resultado.getString("nombre1"));
        usuario.setApellido(resultado.getString("apellido1"));
        usuario.setCedula(resultado.getString("cedula"));
        usuario.setFechaNacimiento(String.valueOf(resultado.getDate("fecha_nacimiento")));
        usuario.setCorreo(resultado.getString("correo"));
        usuario.setEstado("Activo".equals(resultado.getString("estado")));
        usuario.setIdRol(resultado.getInt("id_rol"));
        usuario.setRolNombre(resultado.getString("rol_nombre"));
        return usuario;
    }

    // ---------- UPDATE ----------

    public boolean actualizarUsuario(Usuario usuario) {
        // Si la contraseña llega vacía se conserva la actual (no se toca).
        boolean cambiaContrasena =
                usuario.getContrasena() != null && !usuario.getContrasena().isBlank();

        String sql = cambiaContrasena
                ? """
                        UPDATE usuarios
                        SET nombre1 = ?, apellido1 = ?, cedula = ?, fecha_nacimiento = ?,
                            correo = ?, contrasena = ?, estado = ?, id_rol = ?
                        WHERE id_usuario = ?
                        """
                : """
                        UPDATE usuarios
                        SET nombre1 = ?, apellido1 = ?, cedula = ?, fecha_nacimiento = ?,
                            correo = ?, estado = ?, id_rol = ?
                        WHERE id_usuario = ?
                        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getApellido());
            statement.setString(3, usuario.getCedula());
            statement.setString(4, usuario.getFechaNacimiento());
            statement.setString(5, usuario.getCorreo());

            int indice = 6;
            if (cambiaContrasena) {
                statement.setString(indice++, CODIFICADOR.encode(usuario.getContrasena()));
            }
            statement.setString(indice++, usuario.isEstado() ? "Activo" : "Inactivo");
            statement.setInt(indice++, usuario.getIdRol());
            statement.setInt(indice, usuario.getIdUsuario());

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al actualizar usuario: " + exception.getMessage());
            return false;
        }
    }

    // ---------- DELETE ----------

    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idUsuario);
            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al eliminar usuario: " + exception.getMessage());
            return false;
        }
    }

    // ---------- Utilidades del panel (suspender / activar) ----------

    public boolean cambiarEstado(int idUsuario, boolean activo) {
        String sql = "UPDATE usuarios SET estado = ? WHERE id_usuario = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, activo ? "Activo" : "Inactivo");
            statement.setInt(2, idUsuario);
            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al cambiar estado del usuario: " + exception.getMessage());
            return false;
        }
    }
}
