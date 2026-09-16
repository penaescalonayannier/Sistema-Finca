package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AplicacionLiquidacionSalidaDto;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDetalleDto;
import com.kynsoft.report.domain.dto.EntregaBancoRequest;
import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.ItemSalidaPendienteLiquidacionDto;
import com.kynsoft.report.domain.dto.LiquidarSalidaRequest;
import com.kynsoft.report.domain.dto.SaldoCajaDto;
import com.kynsoft.report.domain.dto.SalidaPendienteLiquidacionDto;
import com.kynsoft.report.domain.dto.TipoMovimiento;
import com.kynsoft.report.domain.dto.TipoMovimientoCaja;
import com.kynsoft.report.domain.services.ILiquidacionSalidaService;
import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import com.kynsoft.report.infrastructure.entity.EntregaBanco;
import com.kynsoft.report.infrastructure.entity.ItemSalida;
import com.kynsoft.report.infrastructure.entity.LiquidacionItemSalida;
import com.kynsoft.report.infrastructure.entity.LiquidacionSalida;
import com.kynsoft.report.infrastructure.entity.MovimientoCaja;
import com.kynsoft.report.infrastructure.entity.Salida;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorDetalleWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.EntregaBancoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.ItemSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.LiquidacionItemSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.LiquidacionSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.MovimientoCajaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.SalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DeudaTrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LiquidacionItemSalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoCajaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SalidaReadDataJPARepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LiquidacionSalidaServiceImpl implements ILiquidacionSalidaService {
    private static final double EPSILON = 0.000001d;

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
    private final EntregaBancoWriteDataJPARepository entregaBancoWriteRepository;

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
                                        EntregaBancoWriteDataJPARepository entregaBancoWriteRepository) {
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
        this.entregaBancoWriteRepository = entregaBancoWriteRepository;
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
                registrarMovimientoCaja(fincaId, aplicacion.getId(), aplicacion.getImporte(),
                        "Cobro en efectivo de " + salida.getNumero());
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
        List<Salida> salidas = fincaId == null
                ? salidaReadRepository.findByFechaAndActivo(fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59))
                : salidaReadRepository.findByFincaIdAndFechaBetween(fincaId, fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));
        return salidas.stream().map(this::aPendienteDto)
                .filter(dto -> dto.getSaldoPendiente() > EPSILON)
                .toList();
    }

    @Override
    public SaldoCajaDto obtenerSaldoCaja(UUID fincaId) {
        if (fincaId == null) throw new IllegalArgumentException("La finca es obligatoria para consultar caja.");
        double efectivo = valor(cajaReadRepository.totalCobradoEfectivoByFincaId(fincaId));
        double entregado = valor(cajaReadRepository.totalEntregadoBancoByFincaId(fincaId));
        return SaldoCajaDto.builder().fincaId(fincaId).efectivoCobrado(efectivo)
                .entregadoBanco(entregado).saldoDisponible(efectivo - entregado).build();
    }

    @Override
    public UUID entregarBanco(EntregaBancoRequest request) {
        if (request == null || request.getFincaId() == null || request.getImporte() == null || request.getImporte() <= 0) {
            throw new IllegalArgumentException("Finca e importe positivo son obligatorios para la entrega al banco.");
        }
        double saldo = valor(cajaReadRepository.saldoByFincaId(request.getFincaId()));
        if (request.getImporte() - saldo > EPSILON) {
            throw new IllegalArgumentException("El importe excede el efectivo disponible en caja.");
        }
        EntregaBanco entrega = new EntregaBanco();
        entrega.setId(UUID.randomUUID());
        entrega.setFincaId(request.getFincaId());
        entrega.setFecha(request.getFecha() == null ? LocalDateTime.now() : request.getFecha());
        entrega.setImporte(request.getImporte());
        entrega.setReferenciaBancaria(texto(request.getReferenciaBancaria()));
        entrega.setEntregadoPor(texto(request.getEntregadoPor()));
        entrega.setRecibidoPor(texto(request.getRecibidoPor()));
        entrega.setObservaciones(texto(request.getObservaciones()));
        entrega.setActivo(true);
        entregaBancoWriteRepository.save(entrega);
        registrarMovimientoCaja(entrega.getFincaId(), null, -entrega.getImporte(), "Entrega a banco: " + texto(entrega.getReferenciaBancaria()), entrega.getId());
        return entrega.getId();
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

    private void registrarMovimientoCaja(UUID fincaId, UUID aplicacionId, double importe, String observaciones) {
        registrarMovimientoCaja(fincaId, aplicacionId, importe, observaciones, null);
    }

    private void registrarMovimientoCaja(UUID fincaId, UUID aplicacionId, double importe, String observaciones, UUID entregaBancoId) {
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setId(UUID.randomUUID());
        movimiento.setFincaId(fincaId);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipo(importe >= 0 ? TipoMovimientoCaja.COBRO_EFECTIVO : TipoMovimientoCaja.ENTREGA_BANCO);
        movimiento.setImporte(importe);
        movimiento.setLiquidacionItemSalidaId(aplicacionId);
        movimiento.setEntregaBancoId(entregaBancoId);
        movimiento.setObservaciones(observaciones);
        cajaWriteRepository.save(movimiento);
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
        }
    }
}
