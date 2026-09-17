package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AplicacionLiquidacionSalidaDto;
import com.kynsoft.report.domain.dto.AperturaCajaRequest;
import com.kynsoft.report.domain.dto.CambioDenominacionesCajaRequest;
import com.kynsoft.report.domain.dto.DenominacionCajaDto;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDetalleDto;
import com.kynsoft.report.domain.dto.EntregaBancoRequest;
import com.kynsoft.report.domain.dto.EntregaBancoResponse;
import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.ItemSalidaPendienteLiquidacionDto;
import com.kynsoft.report.domain.dto.LiquidarSalidaRequest;
import com.kynsoft.report.domain.dto.SaldoCajaDto;
import com.kynsoft.report.domain.dto.SaldoDenominacionCajaDto;
import com.kynsoft.report.domain.dto.SalidaPendienteLiquidacionDto;
import com.kynsoft.report.domain.dto.TipoMovimiento;
import com.kynsoft.report.domain.dto.TipoMovimientoCaja;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.ILiquidacionSalidaService;
import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import com.kynsoft.report.infrastructure.entity.EntregaBanco;
import com.kynsoft.report.infrastructure.entity.ItemSalida;
import com.kynsoft.report.infrastructure.entity.LiquidacionItemSalida;
import com.kynsoft.report.infrastructure.entity.LiquidacionSalida;
import com.kynsoft.report.infrastructure.entity.MovimientoCaja;
import com.kynsoft.report.infrastructure.entity.MovimientoCajaDenominacion;
import com.kynsoft.report.infrastructure.entity.SaldoCajaDenominacion;
import com.kynsoft.report.infrastructure.entity.Salida;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorDetalleWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.EntregaBancoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.ItemSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.LiquidacionItemSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.LiquidacionSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.MovimientoCajaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.MovimientoCajaDenominacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.SaldoCajaDenominacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.SalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DeudaTrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.EntregaBancoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LiquidacionItemSalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoCajaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoCajaDenominacionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SaldoCajaDenominacionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import com.kynsoft.report.infrastructure.security.TenantContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class LiquidacionSalidaServiceImpl implements ILiquidacionSalidaService {
    private static final double EPSILON = 0.000001d;
    private static final Set<Integer> DENOMINACIONES_CUP = Set.of(5, 10, 20, 50, 100, 200, 500,
            1000, 2000, 5000, 10000, 20000);

    private final SalidaReadDataJPARepository salidaReadRepository;
    private final SalidaWriteDataJPARepository salidaWriteRepository;
    private final ItemSalidaWriteDataJPARepository itemWriteRepository;
    private final LiquidacionSalidaWriteDataJPARepository liquidacionWriteRepository;
    private final LiquidacionItemSalidaWriteDataJPARepository aplicacionWriteRepository;
    private final LiquidacionItemSalidaReadDataJPARepository aplicacionReadRepository;
    private final DeudaTrabajadorReadDataJPARepository deudaReadRepository;
    private final DeudaTrabajadorWriteDataJPARepository deudaWriteRepository;
    private final DeudaTrabajadorDetalleWriteDataJPARepository deudaDetalleWriteRepository;
    private final MovimientoCajaReadDataJPARepository cajaReadRepository;
    private final MovimientoCajaWriteDataJPARepository cajaWriteRepository;
    private final MovimientoCajaDenominacionWriteDataJPARepository cajaDenominacionWriteRepository;
    private final MovimientoCajaDenominacionReadDataJPARepository cajaDenominacionReadRepository;
    private final SaldoCajaDenominacionWriteDataJPARepository saldoDenominacionWriteRepository;
    private final SaldoCajaDenominacionReadDataJPARepository saldoDenominacionReadRepository;
    private final EntregaBancoWriteDataJPARepository entregaBancoWriteRepository;
    private final EntregaBancoReadDataJPARepository entregaBancoReadRepository;
    private final AuditoriaTransaccionalService auditoriaTransaccionalService;

    public LiquidacionSalidaServiceImpl(SalidaReadDataJPARepository salidaReadRepository,
                                        SalidaWriteDataJPARepository salidaWriteRepository,
                                        ItemSalidaWriteDataJPARepository itemWriteRepository,
                                        LiquidacionSalidaWriteDataJPARepository liquidacionWriteRepository,
                                        LiquidacionItemSalidaWriteDataJPARepository aplicacionWriteRepository,
                                        LiquidacionItemSalidaReadDataJPARepository aplicacionReadRepository,
                                        DeudaTrabajadorReadDataJPARepository deudaReadRepository,
                                        DeudaTrabajadorWriteDataJPARepository deudaWriteRepository,
                                        DeudaTrabajadorDetalleWriteDataJPARepository deudaDetalleWriteRepository,
                                        MovimientoCajaReadDataJPARepository cajaReadRepository,
                                        MovimientoCajaWriteDataJPARepository cajaWriteRepository,
                                        MovimientoCajaDenominacionWriteDataJPARepository cajaDenominacionWriteRepository,
                                        MovimientoCajaDenominacionReadDataJPARepository cajaDenominacionReadRepository,
                                        SaldoCajaDenominacionWriteDataJPARepository saldoDenominacionWriteRepository,
                                        SaldoCajaDenominacionReadDataJPARepository saldoDenominacionReadRepository,
                                        EntregaBancoWriteDataJPARepository entregaBancoWriteRepository,
                                        EntregaBancoReadDataJPARepository entregaBancoReadRepository,
                                        AuditoriaTransaccionalService auditoriaTransaccionalService) {
        this.salidaReadRepository = salidaReadRepository;
        this.salidaWriteRepository = salidaWriteRepository;
        this.itemWriteRepository = itemWriteRepository;
        this.liquidacionWriteRepository = liquidacionWriteRepository;
        this.aplicacionWriteRepository = aplicacionWriteRepository;
        this.aplicacionReadRepository = aplicacionReadRepository;
        this.deudaReadRepository = deudaReadRepository;
        this.deudaWriteRepository = deudaWriteRepository;
        this.deudaDetalleWriteRepository = deudaDetalleWriteRepository;
        this.cajaReadRepository = cajaReadRepository;
        this.cajaWriteRepository = cajaWriteRepository;
        this.cajaDenominacionWriteRepository = cajaDenominacionWriteRepository;
        this.cajaDenominacionReadRepository = cajaDenominacionReadRepository;
        this.saldoDenominacionWriteRepository = saldoDenominacionWriteRepository;
        this.saldoDenominacionReadRepository = saldoDenominacionReadRepository;
        this.entregaBancoWriteRepository = entregaBancoWriteRepository;
        this.entregaBancoReadRepository = entregaBancoReadRepository;
        this.auditoriaTransaccionalService = auditoriaTransaccionalService;
    }

    @Override
    public UUID liquidar(LiquidarSalidaRequest request) {
        validarSolicitudLiquidacion(request);
        Salida salida = salidaReadRepository.findByIdWithDetails(request.getSalidaId())
                .filter(s -> Boolean.TRUE.equals(s.getActivo()))
                .orElseThrow(() -> new IllegalArgumentException("La salida no existe o está inactiva."));
        UUID fincaId = salida.getFincaProducto() == null || salida.getFincaProducto().getFinca() == null
                ? null : salida.getFincaProducto().getFinca().getId();
        if (fincaId == null) throw new IllegalArgumentException("La salida no tiene una finca válida.");
        TenantValidator.validateWriteAccess(fincaId);

        Map<UUID, Double> importesPorItem = request.getAplicaciones().stream()
                .collect(Collectors.groupingBy(AplicacionLiquidacionSalidaDto::getItemSalidaId,
                        Collectors.summingDouble(aplicacion -> valor(aplicacion.getImporte()))));
        Map<UUID, ItemSalida> itemsBloqueados = new HashMap<>();
        Map<UUID, Double> saldoInicialPorItem = new HashMap<>();
        for (Map.Entry<UUID, Double> entrada : importesPorItem.entrySet()) {
            ItemSalida item = itemWriteRepository.findByIdForUpdate(entrada.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("No existe uno de los ítems seleccionados."));
            if (!salida.getId().equals(item.getSalidaId())) {
                throw new IllegalArgumentException("Todos los ítems deben pertenecer al documento seleccionado.");
            }
            double saldo = saldoPendiente(item);
            if (entrada.getValue() - saldo > EPSILON) {
                throw new IllegalArgumentException("El importe supera el saldo pendiente del ítem seleccionado.");
            }
            itemsBloqueados.put(item.getId(), item);
            saldoInicialPorItem.put(item.getId(), saldo);
        }

        LiquidacionSalida liquidacion = new LiquidacionSalida();
        liquidacion.setId(UUID.randomUUID());
        liquidacion.setSalidaId(salida.getId());
        liquidacion.setFincaId(fincaId);
        liquidacion.setFecha(LocalDateTime.now());
        liquidacion.setEntregadoPor(texto(request.getEntregadoPor()));
        liquidacion.setRecibidoPor(texto(request.getRecibidoPor()));
        liquidacion.setObservaciones(texto(request.getObservaciones()));
        liquidacion.setActivo(true);
        liquidacionWriteRepository.save(liquidacion);

        for (AplicacionLiquidacionSalidaDto aplicacionDto : request.getAplicaciones()) {
            ItemSalida item = itemsBloqueados.get(aplicacionDto.getItemSalidaId());
            LiquidacionItemSalida aplicacion = new LiquidacionItemSalida();
            aplicacion.setId(UUID.randomUUID());
            aplicacion.setLiquidacionSalidaId(liquidacion.getId());
            aplicacion.setItemSalidaId(item.getId());
            aplicacion.setFormaPago(aplicacionDto.getFormaPago());
            aplicacion.setImporte(aplicacionDto.getImporte());
            aplicacion.setReferenciaBancaria(texto(aplicacionDto.getReferenciaBancaria()));
            aplicacionWriteRepository.save(aplicacion);

            registrarPagoTrabajador(item, aplicacion, request.getObservaciones());
            if (aplicacion.getFormaPago() == FormaPago.EFECTIVO) {
                CobroEfectivoDenominado cobro = desgloseCobroEfectivo(aplicacionDto);
                registrarMovimientoCaja(fincaId, aplicacion.getId(), cobro.importeRecibido(),
                        "Cobro en efectivo de " + salida.getNumero(), null, cobro.recibido(), TipoMovimientoCaja.COBRO_EFECTIVO);
                if (cobro.importeVuelto() > EPSILON) {
                    validarDisponibilidadDenominaciones(fincaId, cobro.vuelto());
                    registrarMovimientoCaja(fincaId, aplicacion.getId(), -cobro.importeVuelto(),
                            "Vuelto de cobro en efectivo de " + salida.getNumero(), null, negar(cobro.vuelto()),
                            TipoMovimientoCaja.VUELTO_EFECTIVO);
                }
            }
        }

        for (ItemSalida item : itemsBloqueados.values()) {
            if (saldoInicialPorItem.get(item.getId()) - importesPorItem.get(item.getId()) <= EPSILON) {
                item.setPagado(true);
                itemWriteRepository.save(item);
            }
        }
        boolean salidaSaldada = salida.getItems().stream().allMatch(item -> {
            if (itemsBloqueados.containsKey(item.getId())) {
                return saldoInicialPorItem.get(item.getId()) - importesPorItem.get(item.getId()) <= EPSILON;
            }
            return saldoPendiente(item) <= EPSILON;
        });
        salida.setPagado(salidaSaldada);
        salidaWriteRepository.save(salida);
        return liquidacion.getId();
    }

    @Override
    public List<SalidaPendienteLiquidacionDto> pendientes(UUID fincaId, LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("El rango de fechas es inválido.");
        }
        if (fincaId == null) throw new IllegalArgumentException("La finca es obligatoria para consultar liquidaciones pendientes.");
        TenantValidator.validateReadAccess(fincaId);
        List<Salida> salidas = salidaReadRepository.findByFincaIdAndFechaBetween(
                fincaId, fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));
        return salidas.stream().map(this::aPendienteDto)
                .filter(dto -> dto.getSaldoPendiente() > EPSILON)
                .toList();
    }

    @Override
    public SaldoCajaDto obtenerSaldoCaja(UUID fincaId) {
        if (fincaId == null) throw new IllegalArgumentException("La finca es obligatoria para consultar caja.");
        TenantValidator.validateReadAccess(fincaId);
        double efectivo = valor(cajaReadRepository.totalCobradoEfectivoByFincaId(fincaId));
        double entregado = valor(cajaReadRepository.totalEntregadoBancoByFincaId(fincaId));
        List<SaldoDenominacionCajaDto> denominaciones = saldoDenominacionReadRepository
                .findByFincaIdOrderByDenominacionAsc(fincaId).stream()
                .map(saldo -> SaldoDenominacionCajaDto.builder().denominacion(saldo.getDenominacion())
                        .cantidad(saldo.getCantidad()).importe(importeDenominacion(saldo.getDenominacion(), saldo.getCantidad()))
                        .build()).toList();
        double efectivoDesglosado = denominaciones.stream().mapToDouble(SaldoDenominacionCajaDto::getImporte).sum();
        return SaldoCajaDto.builder().fincaId(fincaId).efectivoCobrado(efectivo)
                .entregadoBanco(entregado).saldoDisponible(efectivo - entregado)
                .denominaciones(denominaciones)
                .pendienteSinDesglose(Math.max(0d, efectivo - entregado - efectivoDesglosado)).build();
    }

    @Override
    public UUID entregarBanco(EntregaBancoRequest request) {
        if (request == null || request.getFincaId() == null) {
            throw new IllegalArgumentException("La finca es obligatoria para la entrega al banco.");
        }
        TenantValidator.validateWriteAccess(request.getFincaId());
        Map<Integer, Integer> denominaciones = validarDenominaciones(request.getDenominaciones(), request.getImporte(),
                "El desglose de billetes de la entrega al banco");
        double importeEntrega = totalDenominaciones(denominaciones);
        double saldo = valor(cajaReadRepository.saldoByFincaId(request.getFincaId()));
        if (importeEntrega - saldo > EPSILON) {
            throw new IllegalArgumentException("El importe excede el efectivo disponible en caja.");
        }
        validarDisponibilidadDenominaciones(request.getFincaId(), denominaciones);
        EntregaBanco entrega = new EntregaBanco();
        entrega.setId(UUID.randomUUID());
        entrega.setFincaId(request.getFincaId());
        entrega.setFecha(request.getFecha() == null ? LocalDateTime.now() : request.getFecha());
        entrega.setImporte(importeEntrega);
        entrega.setReferenciaBancaria(texto(request.getReferenciaBancaria()));
        entrega.setEntregadoPor(texto(request.getEntregadoPor()));
        entrega.setRecibidoPor(texto(request.getRecibidoPor()));
        entrega.setUsuarioId(TenantContext.getUsuarioId());
        entrega.setCreatedAt(LocalDateTime.now());
        entrega.setObservaciones(texto(request.getObservaciones()));
        entrega.setActivo(true);
        entregaBancoWriteRepository.save(entrega);
        registrarMovimientoCaja(entrega.getFincaId(), null, -entrega.getImporte(),
                "Entrega a banco: " + texto(entrega.getReferenciaBancaria()), entrega.getId(), negar(denominaciones));
        return entrega.getId();
    }

    @Override
    public List<EntregaBancoResponse> listarEntregasBanco(UUID fincaId) {
        if (fincaId == null) throw new IllegalArgumentException("La finca es obligatoria para consultar entregas al banco.");
        TenantValidator.validateReadAccess(fincaId);
        List<EntregaBanco> entregas = entregaBancoReadRepository.findByFincaIdAndActivoTrueOrderByFechaDesc(fincaId);
        Map<UUID, UUID> movimientoPorEntrega = cajaReadRepository.findByEntregaBancoIdIn(
                        entregas.stream().map(EntregaBanco::getId).toList()).stream()
                .collect(Collectors.toMap(MovimientoCaja::getEntregaBancoId, MovimientoCaja::getId));
        Map<UUID, List<DenominacionCajaDto>> denominacionesPorEntrega = cajaDenominacionReadRepository
                .findByMovimientoCajaIdIn(movimientoPorEntrega.values()).stream()
                .collect(Collectors.groupingBy(detalle -> detalle.getMovimientoCajaId(), Collectors.mapping(detalle ->
                        DenominacionCajaDto.builder().denominacion(detalle.getDenominacion())
                                .cantidad(Math.abs(detalle.getCantidad())).build(), Collectors.toList())));
        return entregas.stream()
                .map(entrega -> EntregaBancoResponse.builder()
                        .id(entrega.getId()).fincaId(entrega.getFincaId()).fecha(entrega.getFecha())
                        .importe(entrega.getImporte()).referenciaBancaria(entrega.getReferenciaBancaria())
                        .entregadoPor(entrega.getEntregadoPor()).recibidoPor(entrega.getRecibidoPor())
                        .observaciones(entrega.getObservaciones())
                        .denominaciones(denominacionesPorEntrega.getOrDefault(movimientoPorEntrega.get(entrega.getId()), List.of()))
                        .build())
                .toList();
    }

    @Override
    public UUID abrirCajaPorDenominaciones(AperturaCajaRequest request) {
        if (request == null || request.getFincaId() == null) {
            throw new IllegalArgumentException("La finca es obligatoria para la apertura de caja.");
        }
        TenantValidator.validateWriteAccess(request.getFincaId());
        Map<Integer, Integer> denominaciones = validarDenominaciones(request.getDenominaciones(), null,
                "El desglose de la apertura de caja");
        double saldoRegistrado = valor(cajaReadRepository.saldoByFincaId(request.getFincaId()));
        double saldoDesglosado = saldoDenominacionReadRepository.findByFincaIdOrderByDenominacionAsc(request.getFincaId())
                .stream().mapToDouble(saldo -> importeDenominacion(saldo.getDenominacion(), saldo.getCantidad())).sum();
        double pendiente = saldoRegistrado - saldoDesglosado;
        if (pendiente <= EPSILON) {
            throw new IllegalArgumentException("No existe efectivo histórico pendiente de declarar por denominaciones.");
        }
        double apertura = totalDenominaciones(denominaciones);
        if (Math.abs(apertura - pendiente) > EPSILON) {
            throw new IllegalArgumentException("La apertura debe coincidir con el efectivo histórico sin desglose: " + pendiente + ".");
        }
        MovimientoCaja movimiento = registrarMovimientoCaja(request.getFincaId(), null, 0d,
                "Apertura física de caja. " + texto(request.getObservaciones()), null, denominaciones,
                TipoMovimientoCaja.APERTURA_CAJA);
        movimiento.setFecha(request.getFecha() == null ? LocalDateTime.now() : request.getFecha());
        cajaWriteRepository.save(movimiento);
        return movimiento.getId();
    }

    @Override
    public UUID cambiarDenominaciones(CambioDenominacionesCajaRequest request) {
        if (request == null || request.getFincaId() == null) {
            throw new IllegalArgumentException("La finca es obligatoria para el cambio de denominaciones.");
        }
        TenantValidator.validateWriteAccess(request.getFincaId());
        Map<Integer, Integer> entregadas = validarDenominaciones(request.getDenominacionesEntregadas(), null,
                "Las denominaciones entregadas");
        Map<Integer, Integer> recibidas = validarDenominaciones(request.getDenominacionesRecibidas(), null,
                "Las denominaciones recibidas");
        if (Math.abs(totalDenominaciones(entregadas) - totalDenominaciones(recibidas)) > EPSILON) {
            throw new IllegalArgumentException("El cambio de denominaciones debe conservar exactamente el mismo importe.");
        }
        if (entregadas.keySet().stream().anyMatch(recibidas::containsKey)) {
            throw new IllegalArgumentException("Un cambio no puede entregar y recibir la misma denominación; declare solo el saldo físico que cambia.");
        }
        Map<Integer, Integer> movimientoDenominaciones = combinarCambio(entregadas, recibidas);
        if (movimientoDenominaciones.isEmpty()) {
            throw new IllegalArgumentException("El cambio debe modificar al menos una denominación.");
        }
        validarDisponibilidadDenominaciones(request.getFincaId(), entregadas);
        MovimientoCaja movimiento = registrarMovimientoCaja(request.getFincaId(), null, 0d,
                "Cambio de denominaciones. Entregado por: " + texto(request.getEntregadoPor())
                        + ". Recibido por: " + texto(request.getRecibidoPor()) + ". " + texto(request.getObservaciones()),
                null, movimientoDenominaciones, TipoMovimientoCaja.CAMBIO_DENOMINACION);
        movimiento.setFecha(request.getFecha() == null ? LocalDateTime.now() : request.getFecha());
        cajaWriteRepository.save(movimiento);
        return movimiento.getId();
    }

    private SalidaPendienteLiquidacionDto aPendienteDto(Salida salida) {
        List<ItemSalidaPendienteLiquidacionDto> items = new ArrayList<>();
        for (ItemSalida item : salida.getItems()) {
            double total = importeTotal(item);
            double cobrado = valor(aplicacionReadRepository.totalCobradoByItemSalidaId(item.getId()));
            boolean historicoSinTrazabilidad = Boolean.TRUE.equals(item.getPagado()) && cobrado <= EPSILON;
            double saldo = historicoSinTrazabilidad ? 0d : Math.max(0d, total - cobrado);
            if (saldo > EPSILON) {
                items.add(ItemSalidaPendienteLiquidacionDto.builder().itemSalidaId(item.getId())
                        .trabajadorId(item.getTrabajadorId())
                        .trabajadorNombre(item.getTrabajador() == null ? null : item.getTrabajador().getNombre())
                        .productoNombre(item.getFincaProducto() == null || item.getFincaProducto().getProducto() == null
                                ? null : item.getFincaProducto().getProducto().getName())
                        .cantidad(item.getCantidad()).precio(item.getPrecio()).importeTotal(total)
                        .importeCobrado(cobrado).saldoPendiente(saldo).build());
            }
        }
        double total = salida.getItems().stream().mapToDouble(this::importeTotal).sum();
        double saldo = items.stream().mapToDouble(ItemSalidaPendienteLiquidacionDto::getSaldoPendiente).sum();
        return SalidaPendienteLiquidacionDto.builder().salidaId(salida.getId()).numero(salida.getNumero())
                .tipo(salida.getTipo()).destino(salida.getDestino()).fecha(salida.getFecha())
                .fincaNombre(salida.getFincaProducto() != null && salida.getFincaProducto().getFinca() != null
                        ? salida.getFincaProducto().getFinca().getName() : null)
                .importeTotal(total).importeCobrado(total - saldo).saldoPendiente(saldo)
                .estadoCobro(saldo <= EPSILON ? "COBRADO" : saldo < total ? "PARCIAL" : "PENDIENTE")
                .items(items).build();
    }

    private void registrarPagoTrabajador(ItemSalida item, LiquidacionItemSalida aplicacion, String observaciones) {
        if (item.getTrabajadorId() == null) return;
        DeudaTrabajador deuda = deudaReadRepository.findByTrabajadorId(item.getTrabajadorId())
                .orElseThrow(() -> new IllegalArgumentException("No existe deuda activa para el trabajador del ítem."));
        double saldo = valor(deuda.getImporte());
        if (aplicacion.getImporte() - saldo > EPSILON) {
            throw new IllegalArgumentException("El cobro excede la deuda pendiente del trabajador.");
        }
        deuda.setImporte(Math.max(0d, saldo - aplicacion.getImporte()));
        deudaWriteRepository.save(deuda);
        DeudaTrabajadorDetalleDto detalleDto = DeudaTrabajadorDetalleDto.builder()
                .id(UUID.randomUUID()).trabajadorId(item.getTrabajadorId()).importe(-aplicacion.getImporte())
                .fecha(LocalDateTime.now()).activo(true).pagado(true).tipoMovimiento(TipoMovimiento.PAGO)
                .formaPago(aplicacion.getFormaPago()).referenciaBancaria(aplicacion.getReferenciaBancaria())
                .observaciones("Liquidación de salida. " + texto(observaciones)).build();
        com.kynsoft.report.infrastructure.entity.DeudaTrabajadorDetalle detalle =
                new com.kynsoft.report.infrastructure.entity.DeudaTrabajadorDetalle(detalleDto);
        detalle.setLiquidacionItemSalidaId(aplicacion.getId());
        deudaDetalleWriteRepository.save(detalle);
    }

    private MovimientoCaja registrarMovimientoCaja(UUID fincaId, UUID aplicacionId, double importe, String observaciones,
                                                   UUID entregaBancoId, Map<Integer, Integer> denominaciones) {
        return registrarMovimientoCaja(fincaId, aplicacionId, importe, observaciones, entregaBancoId, denominaciones,
                importe >= 0 ? TipoMovimientoCaja.COBRO_EFECTIVO : TipoMovimientoCaja.ENTREGA_BANCO);
    }

    private MovimientoCaja registrarMovimientoCaja(UUID fincaId, UUID aplicacionId, double importe, String observaciones,
                                                   UUID entregaBancoId, Map<Integer, Integer> denominaciones,
                                                   TipoMovimientoCaja tipo) {
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setId(UUID.randomUUID());
        movimiento.setFincaId(fincaId);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipo(tipo);
        movimiento.setImporte(importe);
        movimiento.setUsuarioId(TenantContext.getUsuarioId());
        movimiento.setCreatedAt(LocalDateTime.now());
        movimiento.setLiquidacionItemSalidaId(aplicacionId);
        movimiento.setEntregaBancoId(entregaBancoId);
        movimiento.setObservaciones(observaciones);
        cajaWriteRepository.save(movimiento);
        registrarDenominacionesMovimiento(movimiento.getId(), fincaId, denominaciones);
        auditoriaTransaccionalService.registrarDespuesDeConfirmar(
                TipoAccion.PAYMENT,
                "MOVIMIENTO_CAJA",
                movimiento.getId(),
                "Movimiento de caja " + tipo,
                Map.of("importe", 0d),
                datosAuditoriaCaja(fincaId, tipo, importe, aplicacionId, entregaBancoId, denominaciones));
        return movimiento;
    }

    private Map<String, Object> datosAuditoriaCaja(UUID fincaId, TipoMovimientoCaja tipo, double importe,
                                                    UUID aplicacionId, UUID entregaBancoId,
                                                    Map<Integer, Integer> denominaciones) {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("fincaId", fincaId);
        datos.put("tipo", tipo);
        datos.put("importe", importe);
        datos.put("liquidacionItemSalidaId", aplicacionId);
        datos.put("entregaBancoId", entregaBancoId);
        datos.put("denominaciones", denominaciones);
        return datos;
    }

    private double saldoPendiente(ItemSalida item) {
        if (Boolean.TRUE.equals(item.getPagado()) && valor(aplicacionReadRepository.totalCobradoByItemSalidaId(item.getId())) <= EPSILON) return 0d;
        return Math.max(0d, importeTotal(item) - valor(aplicacionReadRepository.totalCobradoByItemSalidaId(item.getId())));
    }

    private double importeTotal(ItemSalida item) { return valor(item.getCantidad()) * valor(item.getPrecio()); }
    private double valor(Double value) { return value == null ? 0d : value; }
    private String texto(String value) { return value == null ? null : value.trim(); }

    private void validarSolicitudLiquidacion(LiquidarSalidaRequest request) {
        if (request == null || request.getSalidaId() == null || request.getAplicaciones() == null || request.getAplicaciones().isEmpty()) {
            throw new IllegalArgumentException("La salida y al menos una aplicación son obligatorias.");
        }
        for (AplicacionLiquidacionSalidaDto aplicacion : request.getAplicaciones()) {
            if (aplicacion.getItemSalidaId() == null || aplicacion.getImporte() == null || aplicacion.getImporte() <= 0 || aplicacion.getFormaPago() == null) {
                throw new IllegalArgumentException("Cada aplicación requiere ítem, importe positivo y forma de pago.");
            }
            if (aplicacion.getFormaPago() == FormaPago.TRANSFERENCIA &&
                    (aplicacion.getReferenciaBancaria() == null || aplicacion.getReferenciaBancaria().isBlank())) {
                throw new IllegalArgumentException("La referencia bancaria es obligatoria para transferencias.");
            }
            if (aplicacion.getFormaPago() == FormaPago.EFECTIVO) {
                desgloseCobroEfectivo(aplicacion);
            }
        }
    }

    private Map<Integer, Integer> validarDenominaciones(List<DenominacionCajaDto> detalles, Double importeEsperado,
                                                         String etiqueta) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(etiqueta + " es obligatorio.");
        }
        Map<Integer, Integer> resultado = new LinkedHashMap<>();
        for (DenominacionCajaDto detalle : detalles) {
            if (detalle == null || detalle.getDenominacion() == null || !DENOMINACIONES_CUP.contains(detalle.getDenominacion())
                    || detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("Las denominaciones CUP y sus cantidades deben ser válidas y positivas.");
            }
            resultado.merge(detalle.getDenominacion(), detalle.getCantidad(), Math::addExact);
        }
        double total = totalDenominaciones(resultado);
        if (importeEsperado != null && (importeEsperado <= 0 || Math.abs(total - importeEsperado) > EPSILON)) {
            throw new IllegalArgumentException(etiqueta + " debe sumar exactamente " + importeEsperado + ".");
        }
        return resultado;
    }

    private void validarDisponibilidadDenominaciones(UUID fincaId, Map<Integer, Integer> requeridas) {
        for (Map.Entry<Integer, Integer> requerida : requeridas.entrySet()) {
            SaldoCajaDenominacion saldo = saldoDenominacionWriteRepository
                    .findByFincaIdAndDenominacionForUpdate(fincaId, requerida.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("No existen billetes de " + requerida.getKey() + " CUP en caja."));
            if (saldo.getCantidad() < requerida.getValue()) {
                throw new IllegalArgumentException("No hay suficientes billetes de " + requerida.getKey() + " CUP en caja.");
            }
        }
    }

    private void registrarDenominacionesMovimiento(UUID movimientoId, UUID fincaId, Map<Integer, Integer> cantidades) {
        for (Map.Entry<Integer, Integer> detalle : cantidades.entrySet()) {
            MovimientoCajaDenominacion fila = new MovimientoCajaDenominacion();
            fila.setId(UUID.randomUUID());
            fila.setMovimientoCajaId(movimientoId);
            fila.setDenominacion(detalle.getKey());
            fila.setCantidad(detalle.getValue());
            cajaDenominacionWriteRepository.save(fila);
            SaldoCajaDenominacion saldo = saldoDenominacionWriteRepository
                    .findByFincaIdAndDenominacionForUpdate(fincaId, detalle.getKey()).orElseGet(() -> {
                        SaldoCajaDenominacion nuevo = new SaldoCajaDenominacion();
                        nuevo.setId(UUID.randomUUID()); nuevo.setFincaId(fincaId); nuevo.setDenominacion(detalle.getKey()); nuevo.setCantidad(0);
                        return nuevo;
                    });
            int nuevoSaldo = Math.addExact(saldo.getCantidad(), detalle.getValue());
            if (nuevoSaldo < 0) {
                throw new IllegalArgumentException("La salida deja una cantidad negativa de billetes de " + detalle.getKey() + " CUP.");
            }
            saldo.setCantidad(nuevoSaldo);
            saldoDenominacionWriteRepository.save(saldo);
        }
    }

    private Map<Integer, Integer> negar(Map<Integer, Integer> cantidades) {
        return cantidades.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entrada -> -entrada.getValue()));
    }

    private double totalDenominaciones(Map<Integer, Integer> denominaciones) {
        return denominaciones.entrySet().stream().mapToDouble(e -> importeDenominacion(e.getKey(), e.getValue())).sum();
    }

    private double importeDenominacion(Integer denominacion, Integer cantidad) {
        return (double) denominacion * cantidad;
    }

    private CobroEfectivoDenominado desgloseCobroEfectivo(AplicacionLiquidacionSalidaDto aplicacion) {
        List<DenominacionCajaDto> recibidasSolicitud = aplicacion.getDenominacionesRecibidas() == null
                || aplicacion.getDenominacionesRecibidas().isEmpty()
                ? aplicacion.getDenominaciones() : aplicacion.getDenominacionesRecibidas();
        Map<Integer, Integer> recibidas = validarDenominaciones(recibidasSolicitud, null,
                "El desglose de efectivo recibido");
        Map<Integer, Integer> vuelto = aplicacion.getDenominacionesVuelto() == null || aplicacion.getDenominacionesVuelto().isEmpty()
                ? Map.of() : validarDenominaciones(aplicacion.getDenominacionesVuelto(), null, "El desglose del vuelto");
        double importeRecibido = totalDenominaciones(recibidas);
        double importeVuelto = totalDenominaciones(vuelto);
        if (importeRecibido + EPSILON < aplicacion.getImporte()
                || Math.abs(importeRecibido - importeVuelto - aplicacion.getImporte()) > EPSILON) {
            throw new IllegalArgumentException("El efectivo recibido menos el vuelto debe coincidir exactamente con el importe a liquidar.");
        }
        return new CobroEfectivoDenominado(recibidas, vuelto, importeRecibido, importeVuelto);
    }

    private Map<Integer, Integer> combinarCambio(Map<Integer, Integer> entregadas, Map<Integer, Integer> recibidas) {
        Map<Integer, Integer> resultado = new LinkedHashMap<>();
        entregadas.forEach((denominacion, cantidad) -> resultado.merge(denominacion, -cantidad, Integer::sum));
        recibidas.forEach((denominacion, cantidad) -> resultado.merge(denominacion, cantidad, Integer::sum));
        resultado.entrySet().removeIf(entrada -> entrada.getValue() == 0);
        return resultado;
    }

    private record CobroEfectivoDenominado(Map<Integer, Integer> recibido, Map<Integer, Integer> vuelto,
                                            double importeRecibido, double importeVuelto) { }
}
