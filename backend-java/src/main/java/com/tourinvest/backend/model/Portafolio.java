package com.tourinvest.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "portafolios")
public class Portafolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_portafolio")
    private Integer idPortafolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    public Portafolio() {
    }

    public Integer getIdPortafolio() {
        return idPortafolio;
    }

    public void setIdPortafolio(Integer idPortafolio) {
        this.idPortafolio = idPortafolio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}