package com.tourinvest.backend.service;

import com.tourinvest.backend.dto.AlertaRequest;
import com.tourinvest.backend.dto.AlertaResumenDTO;
import com.tourinvest.backend.model.Accion;
import com.tourinvest.backend.model.Alerta;
import com.tourinvest.backend.model.Usuario;
import com.tourinvest.backend.repository.AccionRepository;
import com.tourinvest.backend.repository.AlertaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final AccionRepository accionRepository;

    public AlertaService(AlertaRepository alertaRepository, AccionRepository accionRepository) {
        this.alertaRepository = alertaRepository;
        this.accionRepository = accionRepository;
    }

    public AlertaResumenDTO crear(Usuario usuario, AlertaRequest request) {
        Accion accion = accionRepository.findById(request.getIdAccion())
                .orElseThrow(() -> new NoSuchElementException("La acción indicada no existe"));

        Alerta alerta = new Alerta();
        alerta.setUsuario(usuario);
        alerta.setAccion(accion);
        alerta.setPrecioObjetivo(request.getPrecioObjetivo());
        alerta.setEstado(Alerta.EstadoAlerta.Activa);

        Alerta guardada = alertaRepository.save(alerta);

        return mapearADTO(guardada);
    }

    public List<AlertaResumenDTO> listarPorUsuario(Usuario usuario) {
        return alertaRepository.findByUsuario_IdUsuario(usuario.getIdUsuario()).stream()
                .map(AlertaService::mapearADTO)
                .toList();
    }

    public void cancelar(Usuario usuario, Integer idAlerta) {
        Alerta alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new NoSuchElementException("La alerta no existe"));

        if (!alerta.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalStateException("No puedes cancelar una alerta de otro usuario");
        }

        alerta.setEstado(Alerta.EstadoAlerta.Cancelada);
        alertaRepository.save(alerta);
    }

    public static AlertaResumenDTO mapearADTO(Alerta alerta) {
        return new AlertaResumenDTO(
                alerta.getIdAlerta(),
                alerta.getAccion().getEmpresa().getSimbolo(),
                alerta.getPrecioObjetivo(),
                alerta.getAccion().getPrecio(),
                alerta.getEstado());
    }
}