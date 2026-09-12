package com.tourinvest.servlets;

import com.tourinvest.servlets.util.ConexionJDBC;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RF01: Autenticar usuario (versión Servlet + JSP).
 * GET  /login -> muestra el formulario (login.jsp)
 * POST /login -> valida correo/contraseña contra la tabla 'usuarios'
 *
 * NOTA: valida solo existencia de correo para fines didácticos del módulo;
 * el backend Spring (AuthService) es el que aplica BCrypt en producción.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String sql = "SELECT nombre1, correo FROM usuarios WHERE correo = ?";

        try (Connection conexion = ConexionJDBC.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, correo);

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    HttpSession sesion = request.getSession();
                    sesion.setAttribute("nombreUsuario", resultado.getString("nombre1"));
                    response.sendRedirect(request.getContextPath() + "/empresas");
                    return;
                }
            }
            request.setAttribute("mensajeError", "Correo no registrado.");
        } catch (SQLException e) {
            request.setAttribute("mensajeError", "Error de conexión: " + e.getMessage());
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}