package com.tourinvest.backend.dto;

import com.tourinvest.backend.model.Alerta;

import java.math.BigDecimal;

public class AlertaResumenDTO {

    private final Integer idAlerta;
    private final String simbolo;
    private BigDecimal precioObjetivo;
    private BigDecimal precioActual;
    private final String estado;

    public AlertaResumenDTO(Integer idAlerta, String simbolo, BigDecimal precioObjetivo,
                             BigDecimal precioActual, Alerta.EstadoAlerta estado) {
        this.idAlerta = idAlerta;
        this.simbolo = simbolo;
        this.precioObjetivo = precioObjetivo;
        this.precioActual = precioActual;
        this.estado = estado.name();
    }

    public Integer getIdAlerta() {
        return idAlerta;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public BigDecimal getPrecioObjetivo() {
        return precioObjetivo;
    }

    public BigDecimal getPrecioActual() {
        return precioActual;
    }

    public String getEstado() {
        return estado;
    }

    public void setPrecioObjetivo(BigDecimal precioObjetivo) {
        this.precioObjetivo = precioObjetivo;
    }

    public void setPrecioActual(BigDecimal precioActual) {
        this.precioActual = precioActual;
    }
}