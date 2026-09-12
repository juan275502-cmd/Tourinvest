package com.tourinvest.backend.repository;

import com.tourinvest.backend.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    boolean existsBySimbolo(String simbolo);
    boolean existsBySimboloAndIdEmpresaNot(String simbolo, Integer idEmpresa);
}