package com.tourinvest.backend.repository;

import com.tourinvest.backend.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByUsuario_IdUsuarioOrderByFechaDesc(Integer idUsuario);
}