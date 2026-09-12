package com.tourinvest.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de creación/edición de empresas (Gestión de Empresas, panel de Administrador).
 * El precio corresponde a la cotización (acciones) inicial/actual de la empresa.
 */
public class EmpresaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombre;

    @NotBlank(message = "El símbolo es obligatorio")
    @Size(max = 10, message = "El símbolo no puede superar 10 caracteres")
    private String simbolo;

    @NotBlank(message = "El sector es obligatorio")
    @Size(max = 80, message = "El sector no puede superar 80 caracteres")
    private String sector;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 80, message = "El país no puede superar 80 caracteres")
    private String pais;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    private BigDecimal precio;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}