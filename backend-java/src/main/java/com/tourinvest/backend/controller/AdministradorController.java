package com.tourinvest.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tourinvest.backend.dto.UsuarioResumenDTO;
import com.tourinvest.backend.model.Usuario;
import com.tourinvest.backend.service.AdministradorService;

@RestController
@RequestMapping("/admin/usuarios")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResumenDTO>> listar() {
        return ResponseEntity.ok(administradorService.listarUsuarios());
    }

    @PatchMapping("/{idUsuario}/suspender")
    public ResponseEntity<UsuarioResumenDTO> suspender(@AuthenticationPrincipal Usuario administrador,
                                                          @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(administradorService.suspender(administrador, idUsuario));
    }

    @PatchMapping("/{idUsuario}/activar")
    public ResponseEntity<UsuarioResumenDTO> activar(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(administradorService.activar(idUsuario));
    }
} 