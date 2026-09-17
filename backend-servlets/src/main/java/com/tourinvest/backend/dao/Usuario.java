package com.tourinvest.backend.dao;

/**
 * Modelo simple (POJO) del módulo DAO. Refleja la tabla `usuarios` de
 * query.sql. Es independiente de la entidad JPA
 * com.tourinvest.backend.model.Usuario: el DAO trabaja solo con JDBC y este
 * POJO, tal como pide la guía (paso 6).
 */
public class Usuario {

    private Integer idUsuario;
    private String nombre;          // columna nombre1 (la guía la llama "nombre")
    private String apellido;        // columna apellido1
    private String cedula;
    private String fechaNacimiento; // texto ISO yyyy-MM-dd
    private String correo;
    private String contrasena;
    private boolean estado;         // ENUM('Activo','Inactivo') de la tabla
    private Integer idRol;          // FK a roles (1 Administrador · 2 Analista · 3 Inversionista)
    private String rolNombre;       // solo lectura, llenado con JOIN a roles

    public Usuario() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    public String getNombreCompleto() {
        return ((nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido)).trim();
    }
}
