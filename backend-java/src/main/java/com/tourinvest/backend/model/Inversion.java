package com.tourinvest.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "inversiones")
public class Inversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inversion")
    private Integer idInversion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_portafolio", nullable = false)
    private Portafolio portafolio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_accion", nullable = false)
    private Accion accion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    public Inversion() {
    }

    public Integer getIdInversion() {
        return idInversion;
    }

    public void setIdInversion(Integer idInversion) {
        this.idInversion = idInversion;
    }

    public Portafolio getPortafolio() {
        return portafolio;
    }

    public void setPortafolio(Portafolio portafolio) {
        this.portafolio = portafolio;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    /** Valor actual de esta posición: cantidad * precio actual de la acción. Usado por el dashboard del inversionista. */
    @Transient
    public BigDecimal getValorActual() {
        return accion.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    /** Rendimiento en porcentaje respecto al precio de compra. */
    @Transient
    public BigDecimal getRendimientoPorcentual() {
        BigDecimal costoTotal = precioCompra.multiply(BigDecimal.valueOf(cantidad));
        if (costoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getValorActual().subtract(costoTotal)
                .multiply(BigDecimal.valueOf(100))
                .divide(costoTotal, 4, java.math.RoundingMode.HALF_UP);
    }
}