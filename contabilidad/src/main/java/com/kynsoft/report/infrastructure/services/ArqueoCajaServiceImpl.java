package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.ArqueoCajaDenominacionDto;
import com.kynsoft.report.domain.dto.ArqueoCajaDetalleDto;
import com.kynsoft.report.domain.dto.ArqueoCajaResumenDto;
import com.kynsoft.report.domain.dto.CerrarArqueoCajaRequest;
import com.kynsoft.report.domain.dto.CrearArqueoCajaRequest;
import com.kynsoft.report.domain.dto.DenominacionCajaDto;
import com.kynsoft.report.domain.dto.EstadoArqueoCaja;
import com.kynsoft.report.domain.dto.TipoArqueoCaja;
import com.kynsoft.report.domain.services.IArqueoCajaService;
import com.kynsoft.report.infrastructure.entity.ArqueoCaja;
import com.kynsoft.report.infrastructure.entity.ArqueoCajaDenominacion;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.SaldoCajaDenominacion;
import com.kynsoft.report.infrastructure.repository.command.ArqueoCajaDenominacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.ArqueoCajaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ArqueoCajaDenominacionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ArqueoCajaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoCajaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SaldoCajaDenominacionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import com.kynsoft.report.infrastructure.security.TenantContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Los arqueos son documentos de control: capturan una fotografía de caja y
 * nunca escriben movimientos ni saldos de caja.
 */
@Service
@Transactional
public class ArqueoCajaServiceImpl implements IArqueoCajaService {
    private static final double EPSILON = 0.000001d;
    private static final List<Integer> DENOMINACIONES_CUP = List.of(5, 10, 20, 50, 100, 200, 500,
            1000, 2000, 5000, 10000, 20000);

    private final ArqueoCajaWriteDataJPARepository arqueoWriteRepository;
    private final ArqueoCajaDenominacionWriteDataJPARepository detalleWriteRepository;
    private final ArqueoCajaReadDataJPARepository arqueoReadRepository;
    private final ArqueoCajaDenominacionReadDataJPARepository detalleReadRepository;
    private final SaldoCajaDenominacionReadDataJPARepository saldoDenominacionReadRepository;
    private final MovimientoCajaReadDataJPARepository movimientoCajaReadRepository;
    private final FincaReadDataJPARepository fincaReadRepository;

    public ArqueoCajaServiceImpl(ArqueoCajaWriteDataJPARepository arqueoWriteRepository,
                                 ArqueoCajaDenominacionWriteDataJPARepository detalleWriteRepository,
                                 ArqueoCajaReadDataJPARepository arqueoReadRepository,
                                 ArqueoCajaDenominacionReadDataJPARepository detalleReadRepository,
                                 SaldoCajaDenominacionReadDataJPARepository saldoDenominacionReadRepository,
                                 MovimientoCajaReadDataJPARepository movimientoCajaReadRepository,
                                 FincaReadDataJPARepository fincaReadRepository) {
        this.arqueoWriteRepository = arqueoWriteRepository;
        this.detalleWriteRepository = detalleWriteRepository;
        this.arqueoReadRepository = arqueoReadRepository;
        this.detalleReadRepository = detalleReadRepository;
        this.saldoDenominacionReadRepository = saldoDenominacionReadRepository;
        this.movimientoCajaReadRepository = movimientoCajaReadRepository;
        this.fincaReadRepository = fincaReadRepository;
    }

    @Override
    public UUID crear(CrearArqueoCajaRequest request) {
        if (request == null || request.getFincaId() == null || texto(request.getContadorResponsable()) == null) {
            throw new IllegalArgumentException("La finca y el contador responsable son obligatorios para abrir el arqueo.");
        }
        TenantValidator.validateWriteAccess(request.getFincaId());
        requireFinca(request.getFincaId());
        if (arqueoReadRepository.existsByFincaIdAndEstado(request.getFincaId(), EstadoArqueoCaja.ABIERTO)) {
            throw new IllegalArgumentException("Ya existe un arqueo abierto para esta finca; debe cerrarse antes de iniciar otro.");
        }

        List<Integer> muestra = normalizarMuestra(request.getMuestraDenominaciones());
        Map<Integer, Integer> existencias = existenciasActuales(request.getFincaId());
        TipoArqueoCaja tipo = muestra.size() == DENOMINACIONES_CUP.size() ? TipoArqueoCaja.TOTAL : TipoArqueoCaja.PARCIAL;
        double totalEsperado = muestra.stream().mapToDouble(denominacion -> importe(denominacion, existencias.getOrDefault(denominacion, 0))).sum();
        if (tipo == TipoArqueoCaja.TOTAL) {
            double pendienteSinDesglose = valor(movimientoCajaReadRepository.saldoByFincaId(request.getFincaId())) - totalEsperado;
            if (pendienteSinDesglose > EPSILON) {
                throw new IllegalArgumentException("No puede abrirse un arqueo TOTAL mientras exista efectivo histórico sin desglose por denominaciones: "
                        + pendienteSinDesglose + ". Realice primero la apertura física de caja.");
            }
        }

        ArqueoCaja arqueo = new ArqueoCaja();
        arqueo.setId(UUID.randomUUID());
        arqueo.setNumero(arqueoWriteRepository.siguienteNumero());
        arqueo.setFincaId(request.getFincaId());
        arqueo.setFechaApertura(LocalDateTime.now());
        arqueo.setEstado(EstadoArqueoCaja.ABIERTO);
        arqueo.setTipo(tipo);
        arqueo.setContadorResponsable(texto(request.getContadorResponsable()));
        arqueo.setContadorUsuarioId(TenantContext.getUsuarioId());
        arqueo.setCustodio(texto(request.getCustodio()));
        arqueo.setRecibidoPor(texto(request.getRecibidoPor()));
        arqueo.setObservacionesApertura(texto(request.getObservaciones()));
        arqueo.setTotalEsperado(totalEsperado);
        arqueoWriteRepository.save(arqueo);

        for (Integer denominacion : muestra) {
            ArqueoCajaDenominacion detalle = new ArqueoCajaDenominacion();
            detalle.setId(UUID.randomUUID());
            detalle.setArqueoCajaId(arqueo.getId());
            detalle.setDenominacion(denominacion);
            detalle.setCantidadEsperada(existencias.getOrDefault(denominacion, 0));
            detalleWriteRepository.save(detalle);
        }
        return arqueo.getId();
    }

    @Override
    public void cerrar(UUID id, CerrarArqueoCajaRequest request) {
        if (id == null || request == null || request.getConteoFisico() == null) {
            throw new IllegalArgumentException("El arqueo y su conteo físico son obligatorios.");
        }
        ArqueoCaja arqueo = arqueoWriteRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Arqueo de caja no encontrado."));
        TenantValidator.validateWriteAccess(arqueo.getFincaId());
        if (arqueo.getEstado() != EstadoArqueoCaja.ABIERTO) {
            throw new IllegalArgumentException("El arqueo ya fue cerrado y no puede cerrarse ni reabrirse nuevamente.");
        }
        List<ArqueoCajaDenominacion> detalles = detalleReadRepository.findByArqueoCajaIdOrderByDenominacionAsc(id);
        Map<Integer, Integer> conteoFisico = normalizarConteo(request.getConteoFisico());
        Set<Integer> esperadas = detalles.stream().map(ArqueoCajaDenominacion::getDenominacion).collect(java.util.stream.Collectors.toSet());
        if (!esperadas.equals(conteoFisico.keySet())) {
            throw new IllegalArgumentException("El conteo físico debe declarar exactamente todas las denominaciones seleccionadas al abrir el arqueo.");
        }
        double totalFisico = 0d;
        for (ArqueoCajaDenominacion detalle : detalles) {
            int cantidadFisica = conteoFisico.get(detalle.getDenominacion());
            detalle.setCantidadFisica(cantidadFisica);
            detalleWriteRepository.save(detalle);
            totalFisico += importe(detalle.getDenominacion(), cantidadFisica);
        }
        double diferencia = totalFisico - valor(arqueo.getTotalEsperado());
        String observaciones = texto(request.getObservaciones());
        if (Math.abs(diferencia) > EPSILON && observaciones == null) {
            throw new IllegalArgumentException("Las observaciones de cierre son obligatorias cuando existe una diferencia de caja.");
        }
        arqueo.setFechaCierre(LocalDateTime.now());
        arqueo.setTotalFisico(totalFisico);
        arqueo.setDiferencia(diferencia);
        arqueo.setEstado(EstadoArqueoCaja.CERRADO);
        arqueo.setObservacionesCierre(observaciones);
        arqueoWriteRepository.save(arqueo);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ArqueoCajaResumenDto> listar(UUID fincaId) {
        if (fincaId == null) throw new IllegalArgumentException("La finca es obligatoria para listar arqueos de caja.");
        TenantValidator.validateReadAccess(fincaId);
        Finca finca = requireFinca(fincaId);
        List<ArqueoCaja> arqueos = arqueoReadRepository.findByFincaIdOrderByFechaAperturaDesc(fincaId);
        return arqueos.stream().sorted(Comparator.comparing(ArqueoCaja::getFechaApertura).reversed())
                .map(arqueo -> {
                    List<ArqueoCajaDenominacionDto> denominaciones = detalleReadRepository
                            .findByArqueoCajaIdOrderByDenominacionAsc(arqueo.getId()).stream().map(this::aDetalleDenominacion).toList();
                    return aResumen(arqueo, finca, denominaciones);
                })
                .toList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ArqueoCajaDetalleDto detalle(UUID id) {
        ArqueoCaja arqueo = arqueoReadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Arqueo de caja no encontrado."));
        TenantValidator.validateReadAccess(arqueo.getFincaId());
        Finca finca = requireFinca(arqueo.getFincaId());
        List<ArqueoCajaDenominacionDto> denominaciones = detalleReadRepository.findByArqueoCajaIdOrderByDenominacionAsc(id)
                .stream().map(this::aDetalleDenominacion).toList();
        return ArqueoCajaDetalleDto.builder().id(arqueo.getId()).numero(arqueo.getNumero()).fincaId(arqueo.getFincaId())
                .fincaCodigo(finca.getCode()).fincaNombre(finca.getName())
                .fechaApertura(arqueo.getFechaApertura()).fechaCierre(arqueo.getFechaCierre()).estado(arqueo.getEstado())
                .tipo(arqueo.getTipo()).contadorResponsable(arqueo.getContadorResponsable()).contadorUsuarioId(arqueo.getContadorUsuarioId()).custodio(arqueo.getCustodio())
                .recibidoPor(arqueo.getRecibidoPor()).observaciones(observaciones(arqueo))
                .observacionesApertura(arqueo.getObservacionesApertura()).observacionesCierre(arqueo.getObservacionesCierre())
                .totalEsperado(arqueo.getTotalEsperado()).totalFisico(arqueo.getTotalFisico()).diferencia(arqueo.getDiferencia())
                .denominaciones(denominaciones).build();
    }

    private ArqueoCajaResumenDto aResumen(ArqueoCaja arqueo, Finca finca, List<ArqueoCajaDenominacionDto> denominaciones) {
        return ArqueoCajaResumenDto.builder().id(arqueo.getId()).numero(arqueo.getNumero()).fincaId(arqueo.getFincaId())
                .fincaCodigo(finca.getCode()).fincaNombre(finca.getName())
                .fechaApertura(arqueo.getFechaApertura()).fechaCierre(arqueo.getFechaCierre()).estado(arqueo.getEstado())
                .tipo(arqueo.getTipo()).contadorResponsable(arqueo.getContadorResponsable()).contadorUsuarioId(arqueo.getContadorUsuarioId()).custodio(arqueo.getCustodio())
                .recibidoPor(arqueo.getRecibidoPor()).observaciones(observaciones(arqueo))
                .observacionesApertura(arqueo.getObservacionesApertura()).observacionesCierre(arqueo.getObservacionesCierre())
                .totalEsperado(arqueo.getTotalEsperado()).totalFisico(arqueo.getTotalFisico()).diferencia(arqueo.getDiferencia())
                .denominacionesRevisadas(denominaciones.size()).denominaciones(denominaciones).build();
    }

    private ArqueoCajaDenominacionDto aDetalleDenominacion(ArqueoCajaDenominacion detalle) {
        int esperada = detalle.getCantidadEsperada();
        Integer fisica = detalle.getCantidadFisica();
        return ArqueoCajaDenominacionDto.builder().denominacion(detalle.getDenominacion()).cantidadEsperada(esperada)
                .cantidadFisica(fisica).diferenciaCantidad(fisica == null ? null : fisica - esperada)
                .importeEsperado(importe(detalle.getDenominacion(), esperada))
                .importeFisico(fisica == null ? null : importe(detalle.getDenominacion(), fisica))
                .diferenciaImporte(fisica == null ? null : importe(detalle.getDenominacion(), fisica - esperada)).build();
    }

    private List<Integer> normalizarMuestra(List<Integer> muestraSolicitud) {
        Collection<Integer> origen = muestraSolicitud == null || muestraSolicitud.isEmpty() ? DENOMINACIONES_CUP : muestraSolicitud;
        LinkedHashSet<Integer> resultado = new LinkedHashSet<>(origen);
        if (resultado.isEmpty() || resultado.size() != origen.size() || !DENOMINACIONES_CUP.containsAll(resultado)) {
            throw new IllegalArgumentException("La muestra contiene denominaciones inválidas o repetidas.");
        }
        return resultado.stream().sorted().toList();
    }

    private Map<Integer, Integer> normalizarConteo(List<DenominacionCajaDto> conteo) {
        Map<Integer, Integer> resultado = new HashMap<>();
        for (DenominacionCajaDto fila : conteo) {
            if (fila == null || fila.getDenominacion() == null || !DENOMINACIONES_CUP.contains(fila.getDenominacion())
                    || fila.getCantidad() == null || fila.getCantidad() < 0 || resultado.put(fila.getDenominacion(), fila.getCantidad()) != null) {
                throw new IllegalArgumentException("El conteo físico contiene denominaciones inválidas, repetidas o cantidades negativas.");
            }
        }
        return resultado;
    }

    private Map<Integer, Integer> existenciasActuales(UUID fincaId) {
        Map<Integer, Integer> resultado = new HashMap<>();
        for (SaldoCajaDenominacion saldo : saldoDenominacionReadRepository.findByFincaIdOrderByDenominacionAsc(fincaId)) {
            resultado.put(saldo.getDenominacion(), saldo.getCantidad());
        }
        return resultado;
    }

    private double importe(int denominacion, int cantidad) { return (double) denominacion * cantidad; }
    private double valor(Double valor) { return valor == null ? 0d : valor; }
    private String texto(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
    private String observaciones(ArqueoCaja arqueo) { return arqueo.getObservacionesCierre() != null ? arqueo.getObservacionesCierre() : arqueo.getObservacionesApertura(); }
    private Finca requireFinca(UUID fincaId) { return fincaReadRepository.findById(fincaId).filter(finca -> Boolean.TRUE.equals(finca.getActivo()))
            .orElseThrow(() -> new IllegalArgumentException("La finca no existe o está inactiva.")); }
}
