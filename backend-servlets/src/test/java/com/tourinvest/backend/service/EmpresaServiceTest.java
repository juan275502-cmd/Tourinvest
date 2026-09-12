package com.tourinvest.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tourinvest.backend.dto.EmpresaDetalleDTO;
import com.tourinvest.backend.dto.EmpresaRequest;
import com.tourinvest.backend.dto.EmpresaResumenDTO;
import com.tourinvest.backend.model.Accion;
import com.tourinvest.backend.model.Empresa;
import com.tourinvest.backend.repository.AccionRepository;
import com.tourinvest.backend.repository.AlertaRepository;
import com.tourinvest.backend.repository.EmpresaRepository;
import com.tourinvest.backend.repository.InversionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmpresaService")
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private AccionRepository accionRepository;

    @Mock
    private InversionRepository inversionRepository;

    @Mock
    private AlertaRepository alertaRepository;

    private EmpresaService empresaService;

    private Empresa apple;
    private Accion accionApple;

    @BeforeEach
    void setUp() {
        empresaService = new EmpresaService(empresaRepository, accionRepository,
                inversionRepository, alertaRepository);

        apple = new Empresa();
        apple.setIdEmpresa(1);
        apple.setNombre("Apple");
        apple.setSimbolo("AAPL");
        apple.setSector("Tecnología");
        apple.setPais("Estados Unidos");

        accionApple = new Accion();
        accionApple.setIdAccion(1);
        accionApple.setEmpresa(apple);
        accionApple.setPrecio(new BigDecimal("195.50"));
        accionApple.setVariacion(new BigDecimal("1.80"));
        accionApple.setFechaActualizacion(LocalDateTime.now());
    }

    private EmpresaRequest request(String nombre, String simbolo, String precio) {
        EmpresaRequest r = new EmpresaRequest();
        r.setNombre(nombre);
        r.setSimbolo(simbolo);
        r.setSector("Tecnología");
        r.setPais("Estados Unidos");
        r.setPrecio(new BigDecimal(precio));
        return r;
    }

    // ---------- LISTAR ----------

    @Test
    @DisplayName("listarTodas: con cotización disponible, incluye precio y variación")
    void listarTodas_conCotizacion_incluyePrecioYVariacion() {
        when(empresaRepository.findAll()).thenReturn(List.of(apple));
        when(accionRepository.findFirstByEmpresa_IdEmpresaOrderByFechaActualizacionDesc(1))
                .thenReturn(Optional.of(accionApple));

        List<EmpresaResumenDTO> resultado = empresaService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSimbolo()).isEqualTo("AAPL");
        assertThat(resultado.get(0).getPrecioActual()).isEqualByComparingTo("195.50");
        assertThat(resultado.get(0).getVariacion()).isEqualByComparingTo("1.80");
    }

    @Test
    @DisplayName("listarTodas: sin cotización registrada todavía, precio y variación quedan en null (no lanza excepción)")
    void listarTodas_sinCotizacion_devuelvePrecioNuloSinFallar() {
        when(empresaRepository.findAll()).thenReturn(List.of(apple));
        when(accionRepository.findFirstByEmpresa_IdEmpresaOrderByFechaActualizacionDesc(1))
                .thenReturn(Optional.empty());

        List<EmpresaResumenDTO> resultado = empresaService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPrecioActual()).isNull();
        assertThat(resultado.get(0).getVariacion()).isNull();
    }

    @Test
    @DisplayName("listarTodas: sin empresas registradas, devuelve lista vacía")
    void listarTodas_sinEmpresas_devuelveListaVacia() {
        when(empresaRepository.findAll()).thenReturn(List.of());

        List<EmpresaResumenDTO> resultado = empresaService.listarTodas();

        assertThat(resultado).isEmpty();
    }

    // ---------- DETALLE ----------

    @Test
    @DisplayName("obtenerDetalle: con empresa y cotización existentes, devuelve todos los campos del mockup")
    void obtenerDetalle_empresaYCotizacionExistentes_devuelveDetalleCompleto() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(apple));
        when(accionRepository.findFirstByEmpresa_IdEmpresaOrderByFechaActualizacionDesc(1))
                .thenReturn(Optional.of(accionApple));

        EmpresaDetalleDTO resultado = empresaService.obtenerDetalle(1);

        assertThat(resultado.getNombre()).isEqualTo("Apple");
        assertThat(resultado.getPais()).isEqualTo("Estados Unidos");
        assertThat(resultado.getPrecio()).isEqualByComparingTo("195.50");
        assertThat(resultado.getIdAccion()).isEqualTo(1);
    }

    @Test
    @DisplayName("obtenerDetalle: con empresa inexistente, lanza NoSuchElementException")
    void obtenerDetalle_empresaInexistente_lanzaNoSuchElementException() {
        when(empresaRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.obtenerDetalle(999))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Empresa no encontrada");
    }

    @Test
    @DisplayName("obtenerDetalle: con empresa existente pero sin cotización, lanza NoSuchElementException")
    void obtenerDetalle_sinCotizacion_lanzaNoSuchElementException() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(apple));
        when(accionRepository.findFirstByEmpresa_IdEmpresaOrderByFechaActualizacionDesc(1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.obtenerDetalle(1))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("La empresa no tiene cotización registrada");
    }

    // ---------- CREAR ----------

    @Test
    @DisplayName("crear: normaliza el símbolo a mayúsculas, guarda empresa + cotización y devuelve el detalle")
    void crear_conDatosValidos_guardaEmpresaYCotizacion() {
        when(empresaRepository.existsBySimbolo("PQA")).thenReturn(false);
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(inv -> {
            Empresa e = inv.getArgument(0);
            e.setIdEmpresa(5);
            return e;
        });
        when(accionRepository.save(any(Accion.class))).thenAnswer(inv -> {
            Accion a = inv.getArgument(0);
            a.setIdAccion(9);
            return a;
        });

        EmpresaDetalleDTO resultado = empresaService.crear(request("Prueba QA", "pqa", "99.50"));

        assertThat(resultado.getIdEmpresa()).isEqualTo(5);
        assertThat(resultado.getSimbolo()).isEqualTo("PQA");
        assertThat(resultado.getIdAccion()).isEqualTo(9);
        assertThat(resultado.getPrecio()).isEqualByComparingTo("99.50");
        verify(accionRepository).save(any(Accion.class));
    }

    @Test
    @DisplayName("crear: con símbolo duplicado lanza IllegalArgumentException")
    void crear_conSimboloDuplicado_lanzaExcepcion() {
        when(empresaRepository.existsBySimbolo("AAPL")).thenReturn(true);

        assertThatThrownBy(() -> empresaService.crear(request("Apple CL", "aapl", "50.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("símbolo");
    }

    // ---------- ACTUALIZAR ----------

    @Test
    @DisplayName("actualizar: modifica la empresa y actualiza el precio de la cotización")
    void actualizar_conDatosValidos_actualizaEmpresaYPrecio() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(apple));
        when(empresaRepository.existsBySimboloAndIdEmpresaNot("AAPL", 1)).thenReturn(false);
        when(accionRepository.findFirstByEmpresa_IdEmpresaOrderByFechaActualizacionDesc(1))
                .thenReturn(Optional.of(accionApple));
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accionRepository.save(any(Accion.class))).thenAnswer(inv -> inv.getArgument(0));

        EmpresaDetalleDTO resultado = empresaService.actualizar(1, request("Apple", "AAPL", "210.00"));

        assertThat(resultado.getPrecio()).isEqualByComparingTo("210.00");
        assertThat(resultado.getSector()).isEqualTo("Tecnología");
    }

    @Test
    @DisplayName("actualizar: con símbolo usado por otra empresa lanza IllegalArgumentException")
    void actualizar_conSimboloDeOtraEmpresa_lanzaExcepcion() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(apple));
        when(empresaRepository.existsBySimboloAndIdEmpresaNot("MSFT", 1)).thenReturn(true);

        assertThatThrownBy(() -> empresaService.actualizar(1, request("Apple", "MSFT", "210.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("símbolo");
    }

    // ---------- ELIMINAR ----------

    @Test
    @DisplayName("eliminar: con inversiones asociadas se bloquea (protección de datos)")
    void eliminar_conInversionesAsociadas_seBloquea() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(apple));
        when(accionRepository.findByEmpresa_IdEmpresa(1)).thenReturn(List.of(accionApple));
        when(inversionRepository.existsByAccion_IdAccion(1)).thenReturn(true);

        assertThatThrownBy(() -> empresaService.eliminar(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("inversiones");
    }

    @Test
    @DisplayName("eliminar: con alertas asociadas se bloquea")
    void eliminar_conAlertasAsociadas_seBloquea() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(apple));
        when(accionRepository.findByEmpresa_IdEmpresa(1)).thenReturn(List.of(accionApple));
        when(inversionRepository.existsByAccion_IdAccion(1)).thenReturn(false);
        when(alertaRepository.existsByAccion_IdAccion(1)).thenReturn(true);

        assertThatThrownBy(() -> empresaService.eliminar(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("alertas");
    }

    @Test
    @DisplayName("eliminar: sin referencias, borra cotizaciones y empresa")
    void eliminar_sinReferencias_borraAccionesYEmpresa() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(apple));
        when(accionRepository.findByEmpresa_IdEmpresa(1)).thenReturn(List.of(accionApple));
        when(inversionRepository.existsByAccion_IdAccion(1)).thenReturn(false);
        when(alertaRepository.existsByAccion_IdAccion(1)).thenReturn(false);

        empresaService.eliminar(1);

        verify(accionRepository).deleteAll(List.of(accionApple));
        verify(empresaRepository).delete(apple);
    }
}