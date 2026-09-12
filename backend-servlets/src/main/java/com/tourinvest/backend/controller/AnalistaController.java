package com.tourinvest.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tourinvest.backend.dto.IndicadorLiquidezRequest;
import com.tourinvest.backend.dto.IndicadorLiquidezResponse;
import com.tourinvest.backend.dto.ReporteRequest;
import com.tourinvest.backend.dto.ReporteResumenDTO;
import com.tourinvest.backend.model.Usuario;
import com.tourinvest.backend.service.ReporteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/analista")
public class AnalistaController {

    private final ReporteService reporteService;

    public AnalistaController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/reportes")
    public ResponseEntity<List<ReporteResumenDTO>> listarReportes() {
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    @PostMapping("/reportes")
    public ResponseEntity<ReporteResumenDTO> crearReporte(@AuthenticationPrincipal Usuario analista,
                                                            @Valid @RequestBody ReporteRequest request) {
        ReporteResumenDTO creado = reporteService.crear(analista, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/indicadores/liquidez")
    public ResponseEntity<IndicadorLiquidezResponse> calcularLiquidez(@Valid @RequestBody IndicadorLiquidezRequest request) {
        return ResponseEntity.ok(reporteService.calcularLiquidezCorriente(request));
    }
}