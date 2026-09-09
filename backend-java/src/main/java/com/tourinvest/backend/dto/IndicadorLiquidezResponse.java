package com.tourinvest.backend.dto;

import java.math.BigDecimal;

public class IndicadorLiquidezResponse {

    private final BigDecimal liquidezCorriente;

    public IndicadorLiquidezResponse(BigDecimal liquidezCorriente) {
        this.liquidezCorriente = liquidezCorriente;
    }

    public BigDecimal getLiquidezCorriente() {
        return liquidezCorriente;
    }
}