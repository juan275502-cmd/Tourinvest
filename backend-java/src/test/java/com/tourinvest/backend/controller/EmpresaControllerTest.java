package com.tourinvest.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tourinvest.backend.dto.EmpresaDetalleDTO;
import com.tourinvest.backend.dto.EmpresaResumenDTO;
import com.tourinvest.backend.repository.UsuarioRepository;
import com.tourinvest.backend.security.JwtUtil;
import com.tourinvest.backend.service.EmpresaService;

@WebMvcTest(EmpresaController.class)
@AutoConfigureMockMvc(addFilters = false) // capa web aislada; la cadena JWT real ya está cubierta por JwtUtilTest
@DisplayName("EmpresaController")
class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaService empresaService;

    // Requeridos indirectamente por JwtAuthFilter (ver nota en AuthControllerTest)
    @MockBean
    @SuppressWarnings("unused")
    private JwtUtil jwtUtil;

    @MockBean
    @SuppressWarnings("unused")
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /empresas: responde 200 con la lista de empresas")
    void listar_devuelve200ConListaDeEmpresas() throws Exception {
        when(empresaService.listarTodas()).thenReturn(List.of(
                new EmpresaResumenDTO(1, "Apple", "AAPL", "Tecnología", new BigDecimal("195.50"), new BigDecimal("1.80")),
                new EmpresaResumenDTO(4, "Tesla", "TSLA", "Automotriz", new BigDecimal("250.80"), new BigDecimal("3.25"))));

        mockMvc.perform(get("/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].simbolo").value("AAPL"))
                .andExpect(jsonPath("$[1].simbolo").value("TSLA"));
    }

    @Test
    @DisplayName("GET /empresas: sin empresas registradas, responde 200 con lista vacía")
    void listar_sinEmpresas_devuelve200ConListaVacia() throws Exception {
        when(empresaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /empresas/{id}: con id existente, responde 200 con el detalle completo")
    void detalle_empresaExistente_devuelve200ConDetalle() throws Exception {
        when(empresaService.obtenerDetalle(1)).thenReturn(new EmpresaDetalleDTO(
                1, 1, "Apple", "AAPL", "Tecnología", "Estados Unidos",
                new BigDecimal("195.50"), new BigDecimal("1.80"), LocalDateTime.now()));

        mockMvc.perform(get("/empresas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Apple"))
                .andExpect(jsonPath("$.pais").value("Estados Unidos"))
                .andExpect(jsonPath("$.precio").value(195.50));
    }

    @Test
    @DisplayName("GET /empresas/{id}: con id inexistente, responde 404")
    void detalle_empresaInexistente_devuelve404() throws Exception {
        when(empresaService.obtenerDetalle(999)).thenThrow(new NoSuchElementException("Empresa no encontrada"));

        mockMvc.perform(get("/empresas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Empresa no encontrada"));
    }

}