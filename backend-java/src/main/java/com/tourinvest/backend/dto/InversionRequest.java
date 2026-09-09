package com.tourinvest.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InversionRequest {

    @NotNull(message = "Debes indicar la acción que quieres agregar al portafolio")
    private Integer idAccion;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    public Integer getIdAccion() {
        return idAccion;
    }

    public void setIdAccion(Integer idAccion) {
        this.idAccion = idAccion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
} 