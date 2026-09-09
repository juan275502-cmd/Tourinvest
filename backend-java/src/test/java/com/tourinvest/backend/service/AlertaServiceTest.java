package com.tourinvest.backend.service;

import com.tourinvest.backend.dto.AlertaRequest;
import com.tourinvest.backend.dto.AlertaResumenDTO;
import com.tourinvest.backend.model.*;
import com.tourinvest.backend.repository.AccionRepository;
import com.tourinvest.backend.repository.AlertaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertaService")
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private AccionRepository accionRepository;

    private AlertaService alertaService;

    private Usuario usuario;
    private Usuario otroUsuario;
    private Accion accionTesla;

    @BeforeEach
    void setUp() {
        alertaService = new AlertaService(alertaRepository, accionRepository);

        Rol rolInversionista = new Rol(Rol.NombreRol.Inversionista);
        rolInversionista.setIdRol(3);

        usuario = new Usuario();
        usuario.setIdUsuario(3);
        usuario.setNombre1("Carlos");
        usuario.setRol(rolInversionista);

        otroUsuario = new Usuario();
        otroUsuario.setIdUsuario(7);
        otroUsuario.setNombre1("Ana");
        otroUsuario.setRol(rolInversionista);

        Empresa tesla = new Empresa();
        tesla.setIdEmpresa(4);
        tesla.setNombre("Tesla");
        tesla.setSimbolo("TSLA");

        accionTesla = new Accion();
        accionTesla.setIdAccion(4);
        accionTesla.setEmpresa(tesla);
        accionTesla.setPrecio(new BigDecimal("250.80"));
        accionTesla.setFechaActualizacion(LocalDateTime.now());
    }

    // ---------- CREAR ----------

    @Test
    @DisplayName("crear: con acción existente, guarda la alerta en estado Activa")
    void crear_accionExistente_guardaAlertaActiva() {
        AlertaRequest request = new AlertaRequest();
        request.setIdAccion(4);
        request.setPrecioObjetivo(new BigDecimal("300.00"));

        when(accionRepository.findById(4)).thenReturn(Optional.of(accionTesla));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(inv -> {
            Alerta a = inv.getArgument(0);
            a.setIdAlerta(1);
            return a;
        });

        AlertaResumenDTO resultado = alertaService.crear(usuario, request);

        assertThat(resultado.getSimbolo()).isEqualTo("TSLA");
        assertThat(resultado.getEstado()).isEqualTo("Activa");
        assertThat(resultado.getPrecioObjetivo()).isEqualByComparingTo("300.00");

        verify(alertaRepository).save(argThat(a -> a.getEstado() == Alerta.EstadoAlerta.Activa && a.getUsuario() == usuario));
    }

    @Test
    @DisplayName("crear: con acción inexistente, lanza NoSuchElementException y no guarda nada")
    void crear_accionInexistente_lanzaNoSuchElementException() {
        AlertaRequest request = new AlertaRequest();
        request.setIdAccion(999);
        request.setPrecioObjetivo(new BigDecimal("100.00"));

        when(accionRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertaService.crear(usuario, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("La acción indicada no existe");

        verify(alertaRepository, never()).save(any());
    }

    // ---------- LISTAR ----------

    @Test
    @DisplayName("listarPorUsuario: mapea correctamente cada alerta del usuario")
    void listarPorUsuario_devuelveListaMapeada() {
        Alerta alerta = new Alerta();
        alerta.setIdAlerta(1);
        alerta.setUsuario(usuario);
        alerta.setAccion(accionTesla);
        alerta.setPrecioObjetivo(new BigDecimal("300.00"));
        alerta.setEstado(Alerta.EstadoAlerta.Activa);

        when(alertaRepository.findByUsuario_IdUsuario(3)).thenReturn(List.of(alerta));

        List<AlertaResumenDTO> resultado = alertaService.listarPorUsuario(usuario);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSimbolo()).isEqualTo("TSLA");
    }

    // ---------- CANCELAR ----------

    @Test
    @DisplayName("cancelar: con alerta propia, cambia el estado a Cancelada")
    void cancelar_alertaPropia_cambiaEstadoACancelada() {
        Alerta alerta = new Alerta();
        alerta.setIdAlerta(1);
        alerta.setUsuario(usuario);
        alerta.setAccion(accionTesla);
        alerta.setEstado(Alerta.EstadoAlerta.Activa);

        when(alertaRepository.findById(1)).thenReturn(Optional.of(alerta));

        alertaService.cancelar(usuario, 1);

        verify(alertaRepository).save(argThat(a -> a.getEstado() == Alerta.EstadoAlerta.Cancelada));
    }

    @Test
    @DisplayName("cancelar: con alerta de otro usuario, lanza IllegalStateException y no la modifica")
    void cancelar_alertaDeOtroUsuario_lanzaIllegalStateException() {
        Alerta alertaAjena = new Alerta();
        alertaAjena.setIdAlerta(1);
        alertaAjena.setUsuario(otroUsuario);
        alertaAjena.setAccion(accionTesla);
        alertaAjena.setEstado(Alerta.EstadoAlerta.Activa);

        when(alertaRepository.findById(1)).thenReturn(Optional.of(alertaAjena));

        assertThatThrownBy(() -> alertaService.cancelar(usuario, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No puedes cancelar una alerta de otro usuario");

        verify(alertaRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancelar: con alerta inexistente, lanza NoSuchElementException")
    void cancelar_alertaInexistente_lanzaNoSuchElementException() {
        when(alertaRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertaService.cancelar(usuario, 999))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("La alerta no existe");
    }
}