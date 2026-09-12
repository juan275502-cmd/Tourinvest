package com.tourinvest.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class IndicadorLiquidezRequest {

    @NotNull(message = "El activo corriente es obligatorio")
    private BigDecimal activoCorriente;

    @NotNull(message = "El pasivo corriente es obligatorio")
    private BigDecimal pasivoCorriente;

    public BigDecimal getActivoCorriente() {
        return activoCorriente;
    }

    public void setActivoCorriente(BigDecimal activoCorriente) {
        this.activoCorriente = activoCorriente;
    }

    public BigDecimal getPasivoCorriente() {
        return pasivoCorriente;
    }

    public void setPasivoCorriente(BigDecimal pasivoCorriente) {
        this.pasivoCorriente = pasivoCorriente;
    }
}