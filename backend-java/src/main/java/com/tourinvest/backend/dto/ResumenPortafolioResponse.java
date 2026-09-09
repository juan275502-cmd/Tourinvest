package com.tourinvest.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class ResumenPortafolioResponse {

    private final String nombreUsuario;
    private final BigDecimal valorTotal;
    private final BigDecimal totalInvertido;
    private final BigDecimal rendimientoTotalPorcentual;
    private final List<PosicionDTO> posiciones;
    private final List<AlertaResumenDTO> alertasActivas;

    public ResumenPortafolioResponse(String nombreUsuario, BigDecimal valorTotal, BigDecimal totalInvertido,
                                      BigDecimal rendimientoTotalPorcentual, List<PosicionDTO> posiciones,
                                      List<AlertaResumenDTO> alertasActivas) {
        this.nombreUsuario = nombreUsuario;
        this.valorTotal = valorTotal;
        this.totalInvertido = totalInvertido;
        this.rendimientoTotalPorcentual = rendimientoTotalPorcentual;
        this.posiciones = posiciones;
        this.alertasActivas = alertasActivas;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getTotalInvertido() {
        return totalInvertido;
    }

    public BigDecimal getRendimientoTotalPorcentual() {
        return rendimientoTotalPorcentual;
    }

    public List<PosicionDTO> getPosiciones() {
        return posiciones;
    }

    public List<AlertaResumenDTO> getAlertasActivas() {
        return alertasActivas;
    }
}