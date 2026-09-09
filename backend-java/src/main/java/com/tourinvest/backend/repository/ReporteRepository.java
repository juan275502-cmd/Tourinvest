package com.tourinvest.backend.repository;

import com.tourinvest.backend.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
    List<Reporte> findByUsuario_IdUsuario(Integer idUsuario);
    List<Reporte> findByEmpresa_IdEmpresa(Integer idEmpresa);
}
