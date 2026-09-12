package com.tourinvest.backend.controller;

import com.tourinvest.backend.dto.InversionRequest;
import com.tourinvest.backend.dto.PosicionDTO;
import com.tourinvest.backend.dto.ResumenPortafolioResponse;
import com.tourinvest.backend.model.Usuario;
import com.tourinvest.backend.service.PortafolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inversionista")
public class InversionistaController {

    private final PortafolioService portafolioService;

    public InversionistaController(PortafolioService portafolioService) {
        this.portafolioService = portafolioService;
    }

    /**
     * RF07 - Mostrar portafolio.
     * El usuario autenticado se obtiene del token JWT validado por JwtAuthFilter,
     * nunca de un parámetro de la URL (evita que un usuario consulte el portafolio de otro).
     */
    @GetMapping("/resumen")
    public ResponseEntity<ResumenPortafolioResponse> obtenerResumen(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(portafolioService.obtenerResumen(usuario));
    }

    /**
     * RF07 - Agregar una acción al portafolio (botón "agregar al portafolio" del detalle de acción).
     */
    @PostMapping("/portafolio/inversiones")
    public ResponseEntity<PosicionDTO> agregarInversion(@AuthenticationPrincipal Usuario usuario,
                                                          @Valid @RequestBody InversionRequest request) {
        PosicionDTO posicion = portafolioService.agregarInversion(usuario, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(posicion);
    }
} 