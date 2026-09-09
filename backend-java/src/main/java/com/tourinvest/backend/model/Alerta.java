package com.tourinvest.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer idAlerta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_accion", nullable = false)
    private Accion accion;

    @Column(name = "precio_objetivo", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioObjetivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoAlerta estado = EstadoAlerta.Activa;

    public enum EstadoAlerta {
        Activa,
        Cumplida,
        Cancelada
    }

    public Alerta() {
    }

    public Integer getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(Integer idAlerta) {
        this.idAlerta = idAlerta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public BigDecimal getPrecioObjetivo() {
        return precioObjetivo;
    }

    public void setPrecioObjetivo(BigDecimal precioObjetivo) {
        this.precioObjetivo = precioObjetivo;
    }

    public EstadoAlerta getEstado() {
        return estado;
    }

    public void setEstado(EstadoAlerta estado) {
        this.estado = estado;
    }
}