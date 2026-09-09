package com.tourinvest.backend.dto;

import java.time.LocalDateTime;

public class UsuarioResumenDTO {

    private final Integer idUsuario;
    private final String nombreCompleto;
    private final String correo;
    private final String rol;
    private final String estado;
    private final LocalDateTime fechaRegistro;

    public UsuarioResumenDTO(Integer idUsuario, String nombreCompleto, String correo, String rol,
                              String estado, LocalDateTime fechaRegistro) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}