package com.tourinvest.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourinvest.backend.model.Portafolio;

public interface PortafolioRepository extends JpaRepository<Portafolio, Integer> {
    List<Portafolio> findByUsuario_IdUsuario(Integer idUsuario);
}