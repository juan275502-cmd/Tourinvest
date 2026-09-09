package com.tourinvest.backend.service;

import com.tourinvest.backend.dto.EmpresaDetalleDTO;
import com.tourinvest.backend.dto.EmpresaRequest;
import com.tourinvest.backend.dto.EmpresaResumenDTO;
import com.tourinvest.backend.model.Accion;
import com.tourinvest.backend.model.Empresa;
import com.tourinvest.backend.repository.AccionRepository;
import com.tourinvest.backend.repository.EmpresaRepository;
import com.tourinvest.backend.repository.InversionRepository;
import com.tourinvest.backend.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final AccionRepository accionRepository;
    private final InversionRepository inversionRepository;
    private final AlertaRepository alertaRepository;

    public EmpresaService(EmpresaRepository empresaRepository,
                          AccionRepository accionRepository,
                          InversionRepository inversionRepository,
                          AlertaRepository alertaRepository) {
        this.empresaRepository = empresaRepository;
        this.accionRepository = accionRepository;
        this.inversionRepository = inversionRepository;
        this.alertaRepository = alertaRepository;
    }

    public List<EmpresaResumenDTO> listarTodas() {
        return empresaRepository.findAll().stream()
                .map(empresa -> {
                    Accion ultima = obtenerUltimaCotizacion(empresa.getIdEmpresa());
                    return new EmpresaResumenDTO(
                            empresa.getIdEmpresa(),
                            empresa.getNombre(),
                            empresa.getSimbolo(),
                            empresa.getSector(),
                            ultima != null ? ultima.getPrecio() : null,
                            ultima != null ? ultima.getVariacion() : null);
                })
                .toList();
    }

    public EmpresaDetalleDTO obtenerDetalle(Integer idEmpresa) {
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));

        Accion ultima = accionRepository.findFirstByEmpresa_IdEmpresaOrderByFechaActualizacionDesc(idEmpresa)
                .orElseThrow(() -> new NoSuchElementException("La empresa no tiene cotización registrada"));

        return aDetalle(empresa, ultima);
    }

    // ---------- Gestión de Empresas (panel Administrador) ----------

    @Transactional
    public EmpresaDetalleDTO crear(EmpresaRequest request) {
        String simbolo = request.getSimbolo().trim().toUpperCase();
        if (empresaRepository.existsBySimbolo(simbolo)) {
            throw new IllegalArgumentException("Ya existe una empresa con el símbolo " + simbolo);
        }

        Empresa empresa = new Empresa();
        aplicarDatos(empresa, request, simbolo);
        empresaRepository.save(empresa);

        Accion accion = new Accion();
        accion.setEmpresa(empresa);
        accion.setPrecio(request.getPrecio());
        accion.setVariacion(BigDecimal.ZERO);
        accion.setFechaActualizacion(LocalDateTime.now());
        accionRepository.save(accion);

        return aDetalle(empresa, accion);
    }

    @Transactional
    public EmpresaDetalleDTO actualizar(Integer idEmpresa, EmpresaRequest request) {
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));

        String simbolo = request.getSimbolo().trim().toUpperCase();
        if (empresaRepository.existsBySimboloAndIdEmpresaNot(simbolo, idEmpresa)) {
            throw new IllegalArgumentException("Ya existe otra empresa con el símbolo " + simbolo);
        }

        aplicarDatos(empresa, request, simbolo);
        empresaRepository.save(empresa);

        Accion ultima = accionRepository.findFirstByEmpresa_IdEmpresaOrderByFechaActualizacionDesc(idEmpresa)
                .orElseGet(() -> {
                    Accion nueva = new Accion();
                    nueva.setEmpresa(empresa);
                    nueva.setVariacion(BigDecimal.ZERO);
                    return nueva;
                });
        ultima.setPrecio(request.getPrecio());
        ultima.setFechaActualizacion(LocalDateTime.now());
        accionRepository.save(ultima);

        return aDetalle(empresa, ultima);
    }

    /**
     * Elimina la empresa y sus cotizaciones. Se bloquea si existen inversiones
     * o alertas de usuarios asociadas (FK RESTRICT en query.sql) para no romper
     * la integridad ni borrar datos de portafolios ajenos.
     */
    @Transactional
    public void eliminar(Integer idEmpresa) {
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));

        List<Accion> acciones = accionRepository.findByEmpresa_IdEmpresa(idEmpresa);
        for (Accion accion : acciones) {
            if (inversionRepository.existsByAccion_IdAccion(accion.getIdAccion())) {
                throw new IllegalStateException(
                        "No se puede eliminar \"" + empresa.getNombre()
                                + "\": tiene inversiones de usuarios asociadas.");
            }
            if (alertaRepository.existsByAccion_IdAccion(accion.getIdAccion())) {
                throw new IllegalStateException(
                        "No se puede eliminar \"" + empresa.getNombre() + "\": tiene alertas asociadas.");
            }
        }

        accionRepository.deleteAll(acciones);
        empresaRepository.delete(empresa);
    }

    // ---------- Helpers ----------

    private void aplicarDatos(Empresa empresa, EmpresaRequest request, String simbolo) {
        empresa.setNombre(request.getNombre().trim());
        empresa.setSimbolo(simbolo);
        empresa.setSector(request.getSector().trim());
        empresa.setPais(request.getPais().trim());
    }

    private EmpresaDetalleDTO aDetalle(Empresa empresa, Accion accion) {
        return new EmpresaDetalleDTO(
                empresa.getIdEmpresa(),
                accion.getIdAccion(),
                empresa.getNombre(),
                empresa.getSimbolo(),
                empresa.getSector(),
                empresa.getPais(),
                accion.getPrecio(),
                accion.getVariacion(),
                accion.getFechaActualizacion());
    }

    private Accion obtenerUltimaCotizacion(Integer idEmpresa) {
        return accionRepository.findFirstByEmpresa_IdEmpresaOrderByFechaActualizacionDesc(idEmpresa)
                .orElse(null);
    }
}