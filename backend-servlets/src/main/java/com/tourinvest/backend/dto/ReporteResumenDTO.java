package com.tourinvest.backend.dto;

import java.time.LocalDateTime;

public class ReporteResumenDTO {

    private final Integer idReporte;
    private final String titulo;
    private final String descripcion;
    private final String nombreEmpresa;
    private final String autor;
    private final LocalDateTime fechaGeneracion;

    public ReporteResumenDTO(Integer idReporte, String titulo, String descripcion, String nombreEmpresa,
                              String autor, LocalDateTime fechaGeneracion) {
        this.idReporte = idReporte;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreEmpresa = nombreEmpresa;
        this.autor = autor;
        this.fechaGeneracion = fechaGeneracion;
    }

    public Integer getIdReporte() {
        return idReporte;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public String getAutor() {
        return autor;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
}