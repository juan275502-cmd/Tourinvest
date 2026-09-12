package com.tourinvest.backend.repository;

import com.tourinvest.backend.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(Rol.NombreRol nombre);
}