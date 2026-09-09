package com.tourinvest.backend.service;

import com.tourinvest.backend.dto.UsuarioResumenDTO;
import com.tourinvest.backend.model.Rol;
import com.tourinvest.backend.model.Usuario;
import com.tourinvest.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdministradorService")
class AdministradorServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private AdministradorService administradorService;

    private Usuario administrador;
    private Usuario inversionista;

    @BeforeEach
    void setUp() {
        administradorService = new AdministradorService(usuarioRepository);

        Rol rolAdmin = new Rol(Rol.NombreRol.Administrador);
        rolAdmin.setIdRol(1);

        Rol rolInversionista = new Rol(Rol.NombreRol.Inversionista);
        rolInversionista.setIdRol(3);

        administrador = new Usuario();
        administrador.setIdUsuario(1);
        administrador.setNombre1("Carlos");
        administrador.setApellido1("Ruiz");
        administrador.setCorreo("carlos@tourinvest.com");
        administrador.setRol(rolAdmin);
        administrador.setEstado(Usuario.EstadoUsuario.Activo);

        inversionista = new Usuario();
        inversionista.setIdUsuario(3);
        inversionista.setNombre1("Juan");
        inversionista.setApellido1("Fuentes");
        inversionista.setCorreo("juan@tourinvest.com");
        inversionista.setRol(rolInversionista);
        inversionista.setEstado(Usuario.EstadoUsuario.Activo);
    }

    @Test
    @DisplayName("listarUsuarios: mapea nombre completo, rol y estado correctamente")
    void listarUsuarios_devuelveListaMapeadaCorrectamente() {
        when(usuarioRepository.findAll()).thenReturn(List.of(administrador, inversionista));

        List<UsuarioResumenDTO> resultado = administradorService.listarUsuarios();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombreCompleto()).isEqualTo("Carlos Ruiz");
        assertThat(resultado.get(0).getRol()).isEqualTo("Administrador");
        assertThat(resultado.get(1).getEstado()).isEqualTo("Activo");
    }

    // ---------- SUSPENDER ----------

    @Test
    @DisplayName("suspender: con usuario distinto al administrador, cambia el estado a Inactivo")
    void suspender_usuarioDistinto_cambiaEstadoAInactivo() {
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(inversionista));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResumenDTO resultado = administradorService.suspender(administrador, 3);

        assertThat(resultado.getEstado()).isEqualTo("Inactivo");
        verify(usuarioRepository).save(argThat(u -> u.getEstado() == Usuario.EstadoUsuario.Inactivo));
    }

    @Test
    @DisplayName("suspender: si el administrador intenta suspenderse a sí mismo, lanza IllegalStateException")
    void suspender_intentaAutoSuspenderse_lanzaIllegalStateException() {
        assertThatThrownBy(() -> administradorService.suspender(administrador, administrador.getIdUsuario()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No puedes suspender tu propia cuenta de administrador");

        verify(usuarioRepository, never()).findById(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("suspender: con usuario inexistente, lanza NoSuchElementException")
    void suspender_usuarioInexistente_lanzaNoSuchElementException() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> administradorService.suspender(administrador, 999))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Usuario no encontrado");
    }

    // ---------- ACTIVAR ----------

    @Test
    @DisplayName("activar: cambia el estado del usuario a Activo")
    void activar_usuarioExistente_cambiaEstadoAActivo() {
        inversionista.setEstado(Usuario.EstadoUsuario.Inactivo);
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(inversionista));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResumenDTO resultado = administradorService.activar(3);

        assertThat(resultado.getEstado()).isEqualTo("Activo");
    }

    @Test
    @DisplayName("activar: con usuario inexistente, lanza NoSuchElementException")
    void activar_usuarioInexistente_lanzaNoSuchElementException() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> administradorService.activar(999))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Usuario no encontrado");
    }
} 