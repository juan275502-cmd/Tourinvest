package com.tourinvest.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EmpresaDetalleDTO {

    private final Integer idEmpresa;
    private Integer idAccion;
    private final String nombre;
    private final String simbolo;
    private String sector;
    private String pais;
    private BigDecimal precio;
    private BigDecimal variacion;
    private final LocalDateTime fechaActualizacion;

    public EmpresaDetalleDTO(Integer idEmpresa, Integer idAccion, String nombre, String simbolo, String sector,
                              String pais, BigDecimal precio, BigDecimal variacion, LocalDateTime fechaActualizacion) {
        this.idEmpresa = idEmpresa;
        this.idAccion = idAccion;
        this.nombre = nombre;
        this.simbolo = simbolo;
        this.sector = sector;
        this.pais = pais;
        this.precio = precio;
        this.variacion = variacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public Integer getIdAccion() {
        return idAccion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getSector() {
        return sector;
    }

    public String getPais() {
        return pais;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public BigDecimal getVariacion() {
        return variacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setIdAccion(Integer idAccion) {
        this.idAccion = idAccion;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public void setVariacion(BigDecimal variacion) {
        this.variacion = variacion;
    }
}