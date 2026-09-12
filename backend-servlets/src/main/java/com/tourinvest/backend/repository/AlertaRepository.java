package com.tourinvest.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourinvest.backend.model.Alerta;

public interface AlertaRepository extends JpaRepository<Alerta, Integer> {
    List<Alerta> findByUsuario_IdUsuario(Integer idUsuario);
    List<Alerta> findByUsuario_IdUsuarioAndEstado(Integer idUsuario, Alerta.EstadoAlerta estado);
    boolean existsByAccion_IdAccion(Integer idAccion);
}