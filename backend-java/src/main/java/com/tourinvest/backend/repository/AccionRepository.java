package com.tourinvest.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourinvest.backend.model.Accion;

public interface AccionRepository extends JpaRepository<Accion, Integer> {
    List<Accion> findByEmpresa_IdEmpresa(Integer idEmpresa);
    Optional<Accion> findFirstByEmpresa_IdEmpresaOrderByFechaActualizacionDesc(Integer idEmpresa);
}