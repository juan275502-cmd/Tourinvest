package com.tourinvest.backend.service;

import com.tourinvest.backend.dto.AlertaResumenDTO;
import com.tourinvest.backend.dto.InversionRequest;
import com.tourinvest.backend.dto.PosicionDTO;
import com.tourinvest.backend.dto.ResumenPortafolioResponse;
import com.tourinvest.backend.model.Accion;
import com.tourinvest.backend.model.Alerta;
import com.tourinvest.backend.model.Inversion;
import com.tourinvest.backend.model.Portafolio;
import com.tourinvest.backend.model.Usuario;
import com.tourinvest.backend.repository.AccionRepository;
import com.tourinvest.backend.repository.AlertaRepository;
import com.tourinvest.backend.repository.InversionRepository;
import com.tourinvest.backend.repository.PortafolioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PortafolioService {

    private final InversionRepository inversionRepository;
    private final AlertaRepository alertaRepository;
    private final PortafolioRepository portafolioRepository;
    private final AccionRepository accionRepository;

    public PortafolioService(InversionRepository inversionRepository, AlertaRepository alertaRepository,
                              PortafolioRepository portafolioRepository, AccionRepository accionRepository) {
        this.inversionRepository = inversionRepository;
        this.alertaRepository = alertaRepository;
        this.portafolioRepository = portafolioRepository;
        this.accionRepository = accionRepository;
    }

    /**
     * RF07 - Agregar una acción al portafolio. El precio de compra se toma del precio
     * de mercado vigente de la acción (nunca del cliente), y si el usuario aún no tiene
     * un portafolio se le crea uno por defecto ("Portafolio Principal").
     */
    public PosicionDTO agregarInversion(Usuario usuario, InversionRequest request) {
        Portafolio portafolio = portafolioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .stream()
                .findFirst()
                .orElseGet(() -> crearPortafolioPorDefecto(usuario));

        Accion accion = accionRepository.findById(request.getIdAccion())
                .orElseThrow(() -> new NoSuchElementException("La acción indicada no existe"));

        Inversion inversion = new Inversion();
        inversion.setPortafolio(portafolio);
        inversion.setAccion(accion);
        inversion.setCantidad(request.getCantidad());
        inversion.setPrecioCompra(accion.getPrecio());
        inversion.setFechaCompra(LocalDate.now());

        Inversion guardada = inversionRepository.save(inversion);
        return mapearAPosicion(guardada);
    }

    private Portafolio crearPortafolioPorDefecto(Usuario usuario) {
        Portafolio nuevo = new Portafolio();
        nuevo.setUsuario(usuario);
        nuevo.setNombre("Portafolio Principal");
        return portafolioRepository.save(nuevo);
    }

    public ResumenPortafolioResponse obtenerResumen(Usuario usuario) {
        List<Inversion> inversiones = inversionRepository.findByUsuario(usuario.getIdUsuario());

        List<PosicionDTO> posiciones = inversiones.stream()
                .map(this::mapearAPosicion)
                .toList();

        BigDecimal valorTotal = inversiones.stream()
                .map(Inversion::getValorActual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInvertido = inversiones.stream()
                .map(i -> i.getPrecioCompra().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rendimientoTotal = calcularRendimiento(valorTotal, totalInvertido);

        List<AlertaResumenDTO> alertasActivas = alertaRepository
                .findByUsuario_IdUsuarioAndEstado(usuario.getIdUsuario(), Alerta.EstadoAlerta.Activa)
                .stream()
                .map(AlertaService::mapearADTO)
                .toList();

        return new ResumenPortafolioResponse(
                usuario.getNombre1(), valorTotal, totalInvertido, rendimientoTotal, posiciones, alertasActivas);
    }

    private PosicionDTO mapearAPosicion(Inversion inversion) {
        return new PosicionDTO(
                inversion.getAccion().getEmpresa().getNombre(),
                inversion.getAccion().getEmpresa().getSimbolo(),
                inversion.getCantidad(),
                inversion.getPrecioCompra(),
                inversion.getAccion().getPrecio(),
                inversion.getValorActual(),
                inversion.getRendimientoPorcentual()
        );
    }

    private BigDecimal calcularRendimiento(BigDecimal valorTotal, BigDecimal totalInvertido) {
        if (totalInvertido.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return valorTotal.subtract(totalInvertido)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalInvertido, 4, RoundingMode.HALF_UP);
    }
} 