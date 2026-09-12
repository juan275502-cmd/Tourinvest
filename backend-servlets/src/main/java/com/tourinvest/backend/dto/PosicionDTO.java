package com.tourinvest.backend.dto;

import java.math.BigDecimal;

public class PosicionDTO {

    private final String nombreEmpresa;
    private String simbolo;
    private final Integer cantidad;
    private final BigDecimal precioCompra;
    private BigDecimal precioActual;
    private BigDecimal valorActual;
    private BigDecimal rendimientoPorcentual;

    public PosicionDTO(String nombreEmpresa, String simbolo, Integer cantidad,
                        BigDecimal precioCompra, BigDecimal precioActual,
                        BigDecimal valorActual, BigDecimal rendimientoPorcentual) {
        this.nombreEmpresa = nombreEmpresa;
        this.simbolo = simbolo;
        this.cantidad = cantidad;
        this.precioCompra = precioCompra;
        this.precioActual = precioActual;
        this.valorActual = valorActual;
        this.rendimientoPorcentual = rendimientoPorcentual;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public BigDecimal getPrecioActual() {
        return precioActual;
    }

    public BigDecimal getValorActual() {
        return valorActual;
    }

    public BigDecimal getRendimientoPorcentual() {
        return rendimientoPorcentual;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public void setPrecioActual(BigDecimal precioActual) {
        this.precioActual = precioActual;
    }

    public void setValorActual(BigDecimal valorActual) {
        this.valorActual = valorActual;
    }

    public void setRendimientoPorcentual(BigDecimal rendimientoPorcentual) {
        this.rendimientoPorcentual = rendimientoPorcentual;
    }
}