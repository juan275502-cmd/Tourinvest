package com.tourinvest.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tourinvest.backend.model.Inversion;

public interface InversionRepository extends JpaRepository<Inversion, Integer> {

    List<Inversion> findByPortafolio_IdPortafolio(Integer idPortafolio);

    boolean existsByAccion_IdAccion(Integer idAccion);

    // Todas las inversiones de un usuario, sin importar en cuál de sus portafolios estén.
    // Necesario para el resumen general del dashboard del inversionista.
    @Query("SELECT i FROM Inversion i WHERE i.portafolio.usuario.idUsuario = :idUsuario")
    List<Inversion> findByUsuario(@Param("idUsuario") Integer idUsuario);
}