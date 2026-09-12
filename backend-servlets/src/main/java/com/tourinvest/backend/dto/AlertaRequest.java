package com.tourinvest.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AlertaRequest {

    @NotNull(message = "Debes indicar la acción sobre la que quieres crear la alerta")
    private Integer idAccion;

    @NotNull(message = "El precio objetivo es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio objetivo debe ser mayor que cero")
    private BigDecimal precioObjetivo;

    public Integer getIdAccion() {
        return idAccion;
    }

    public void setIdAccion(Integer idAccion) {
        this.idAccion = idAccion;
    }

    public BigDecimal getPrecioObjetivo() {
        return precioObjetivo;
    }

    public void setPrecioObjetivo(BigDecimal precioObjetivo) {
        this.precioObjetivo = precioObjetivo;
    }
}