package com.tourinvest.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReporteRequest {

    @NotNull(message = "Debes indicar la empresa a analizar")
    private Integer idEmpresa;

    @NotBlank(message = "El título del reporte es obligatorio")
    private String titulo;

    private String descripcion;

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}