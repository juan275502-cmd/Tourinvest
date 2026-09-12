package com.tourinvest.backend.controller;

import com.tourinvest.backend.dto.AlertaRequest;
import com.tourinvest.backend.dto.AlertaResumenDTO;
import com.tourinvest.backend.model.Usuario;
import com.tourinvest.backend.service.AlertaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inversionista/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public ResponseEntity<List<AlertaResumenDTO>> listar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(alertaService.listarPorUsuario(usuario));
    }

    @PostMapping
    public ResponseEntity<AlertaResumenDTO> crear(@AuthenticationPrincipal Usuario usuario,
                                                    @Valid @RequestBody AlertaRequest request) {
        AlertaResumenDTO creada = alertaService.crear(usuario, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PatchMapping("/{idAlerta}/cancelar")
    public ResponseEntity<Void> cancelar(@AuthenticationPrincipal Usuario usuario, @PathVariable Integer idAlerta) {
        alertaService.cancelar(usuario, idAlerta);
        return ResponseEntity.noContent().build();
    }
}