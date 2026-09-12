package com.tourinvest.backend.dto;

import java.math.BigDecimal;

public class EmpresaResumenDTO {

    private final Integer idEmpresa;
    private String nombre;
    private String simbolo;
    private String sector;
    private BigDecimal precioActual;
    private BigDecimal variacion;

    public EmpresaResumenDTO(Integer idEmpresa, String nombre, String simbolo, String sector,
                              BigDecimal precioActual, BigDecimal variacion) {
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
        this.simbolo = simbolo;
        this.sector = sector;
        this.precioActual = precioActual;
        this.variacion = variacion;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
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

    public BigDecimal getPrecioActual() {
        return precioActual;
    }

    public BigDecimal getVariacion() {
        return variacion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setPrecioActual(BigDecimal precioActual) {
        this.precioActual = precioActual;
    }

    public void setVariacion(BigDecimal variacion) {
        this.variacion = variacion;
    }
}