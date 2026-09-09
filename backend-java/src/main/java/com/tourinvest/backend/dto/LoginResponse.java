package com.tourinvest.backend.dto;

public class LoginResponse {

    private final String token;
    private final String nombre1;
    private final String correo;
    private final String rol; // Administrador | Analista | Inversionista -> el frontend enruta el dashboard con esto

    public LoginResponse(String token, String nombre1, String correo, String rol) {
        this.token = token;
        this.nombre1 = nombre1;
        this.correo = correo;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public String getNombre1() {
        return nombre1;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }
} 