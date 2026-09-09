package com.tourinvest.backend.controller;

import com.tourinvest.backend.dto.EmpresaDetalleDTO;
import com.tourinvest.backend.dto.EmpresaRequest;
import com.tourinvest.backend.dto.EmpresaResumenDTO;
import com.tourinvest.backend.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResumenDTO>> listar() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    @GetMapping("/{idEmpresa}")
    public ResponseEntity<EmpresaDetalleDTO> detalle(@PathVariable Integer idEmpresa) {
        return ResponseEntity.ok(empresaService.obtenerDetalle(idEmpresa));
    }

    /** Crear empresa (Gestión de Empresas, Administrador). */
    @PostMapping
    public ResponseEntity<EmpresaDetalleDTO> crear(@Valid @RequestBody EmpresaRequest request) {
        EmpresaDetalleDTO creado = empresaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /** Editar empresa (Gestión de Empresas, Administrador). */
    @PutMapping("/{idEmpresa}")
    public ResponseEntity<EmpresaDetalleDTO> actualizar(@PathVariable Integer idEmpresa,
                                                        @Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(empresaService.actualizar(idEmpresa, request));
    }

    /** Eliminar empresa (Gestión de Empresas, Administrador). Se bloquea si hay inversiones/alertas asociadas. */
    @DeleteMapping("/{idEmpresa}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Integer idEmpresa) {
        empresaService.eliminar(idEmpresa);
        return ResponseEntity.ok(Map.of("mensaje", "Empresa eliminada correctamente."));
    }
}