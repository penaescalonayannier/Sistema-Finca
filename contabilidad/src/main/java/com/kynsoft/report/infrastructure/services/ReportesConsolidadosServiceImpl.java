package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.dto.TipoSalida;
import com.kynsoft.report.domain.dto.reportes.*;
import com.kynsoft.report.domain.services.IReportesConsolidadosService;
import com.kynsoft.report.infrastructure.entity.*;
import com.kynsoft.report.infrastructure.repository.query.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportesConsolidadosServiceImpl implements IReportesConsolidadosService {

    private final DeudaTrabajadorReadDataJPARepository deudaRepository;
    private final TrabajadorReadDataJPARepository trabajadorRepository;
    private final PagoDeudaReadDataJPARepository pagoDeudaRepository;
    private final SalidaReadDataJPARepository salidaRepository;
    private final FincaReadDataJPARepository fincaRepository;
    private final MovimientoStockReadDataJPARepository movimientoStockRepository;
    private final FincaProductoReadDataJPARepository fincaProductoRepository;
    private final ProductoReadDataJPARepository productoRepository;

    public ReportesConsolidadosServiceImpl(
            DeudaTrabajadorReadDataJPARepository deudaRepository,
            TrabajadorReadDataJPARepository trabajadorRepository,
            PagoDeudaReadDataJPARepository pagoDeudaRepository,
            SalidaReadDataJPARepository salidaRepository,
            FincaReadDataJPARepository fincaRepository,
            MovimientoStockReadDataJPARepository movimientoStockRepository,
            FincaProductoReadDataJPARepository fincaProductoRepository,
            ProductoReadDataJPARepository productoRepository) {
        this.deudaRepository = deudaRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.pagoDeudaRepository = pagoDeudaRepository;
        this.salidaRepository = salidaRepository;
        this.fincaRepository = fincaRepository;
        this.movimientoStockRepository = movimientoStockRepository;
        this.fincaProductoRepository = fincaProductoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public ReporteDeudasPendientesDto getDeudasPendientes(
            UUID fincaId,
            Double montoMinimo,
            Double montoMaximo,
            boolean incluirHistorial) {

        List<DeudaTrabajador> todasDeudas = deudaRepository.findAll();

        // Filter by finca if provided
        if (fincaId != null) {
            todasDeudas = todasDeudas.stream()
                    .filter(d -> d.getTrabajador() != null && fincaId.equals(d.getTrabajador().getFincaId()))
                    .collect(Collectors.toList());
        }

        // Filter by monto range
        if (montoMinimo != null) {
            todasDeudas = todasDeudas.stream()
                    .filter(d -> d.getImporte() >= montoMinimo)
                    .collect(Collectors.toList());
        }
        if (montoMaximo != null) {
            todasDeudas = todasDeudas.stream()
                    .filter(d -> d.getImporte() <= montoMaximo)
                    .collect(Collectors.toList());
        }

        // Separate deudas con saldo > 0
        List<DeudaTrabajador> deudasConSaldo = todasDeudas.stream()
                .filter(d -> d.getImporte() != null && d.getImporte() > 0)
                .sorted((a, b) -> Double.compare(b.getImporte(), a.getImporte()))
                .collect(Collectors.toList());

        // Calculate resumen
        int totalTrabajadores = (int) trabajadorRepository.count();
        int trabajadoresConDeuda = deudasConSaldo.size();
        double montoTotal = deudasConSaldo.stream().mapToDouble(DeudaTrabajador::getImporte).sum();
        double promedio = trabajadoresConDeuda > 0 ? montoTotal / trabajadoresConDeuda : 0;
        double maxDeuda = deudasConSaldo.stream().mapToDouble(DeudaTrabajador::getImporte).max().orElse(0);
        double minDeuda = deudasConSaldo.stream().mapToDouble(DeudaTrabajador::getImporte).min().orElse(0);

        ResumenDeudasDto resumen = ResumenDeudasDto.builder()
                .totalTrabajadores(totalTrabajadores)
                .trabajadoresConDeuda(trabajadoresConDeuda)
                .trabajadoresSinDeuda(totalTrabajadores - trabajadoresConDeuda)
                .montoTotalDeuda(montoTotal)
                .promedioDeuda(Math.round(promedio * 100.0) / 100.0)
                .deudaMaxima(maxDeuda)
                .deudaMinima(minDeuda)
                .build();

        // Group by finca
        Map<UUID, List<DeudaTrabajador>> porFincaMap = deudasConSaldo.stream()
                .filter(d -> d.getTrabajador() != null && d.getTrabajador().getFincaId() != null)
                .collect(Collectors.groupingBy(d -> d.getTrabajador().getFincaId()));

        List<DeudaPorFincaDto> porFinca = new ArrayList<>();
        for (Map.Entry<UUID, List<DeudaTrabajador>> entry : porFincaMap.entrySet()) {
            Finca finca = fincaRepository.findById(entry.getKey()).orElse(null);
            porFinca.add(DeudaPorFincaDto.builder()
                    .fincaId(entry.getKey())
                    .fincaCode(finca != null ? finca.getCode() : null)
                    .fincaName(finca != null ? finca.getName() : null)
                    .trabajadoresConDeuda(entry.getValue().size())
                    .montoTotal(entry.getValue().stream().mapToDouble(DeudaTrabajador::getImporte).sum())
                    .build());
        }
        porFinca.sort((a, b) -> Double.compare(b.getMontoTotal(), a.getMontoTotal()));

        // Build deudas detail
        List<DeudaDetalleDto> deudas = new ArrayList<>();
        for (DeudaTrabajador deuda : deudasConSaldo) {
            Trabajador trab = deuda.getTrabajador();
            if (trab == null) continue;

            DeudaDetalleDto.DeudaDetalleDtoBuilder builder = DeudaDetalleDto.builder()
                    .trabajadorId(trab.getId())
                    .trabajadorNombre(trab.getNombre())
                    .trabajadorRuc(trab.getRuc())
                    .fincaId(trab.getFincaId())
                    .fincaName(trab.getFinca() != null ? trab.getFinca().getName() : null)
                    .monto(deuda.getImporte());

            if (incluirHistorial) {
                List<PagoDeuda> pagos = pagoDeudaRepository.findByTrabajadorIdOrderByFechaDesc(trab.getId());
                if (!pagos.isEmpty()) {
                    PagoDeuda ultimoPago = pagos.get(0);
                    builder.ultimoPagoFecha(ultimoPago.getFecha().toLocalDate())
                            .ultimoPagoMonto(ultimoPago.getMonto());
                }
            }

            deudas.add(builder.build());
        }

        return ReporteDeudasPendientesDto.builder()
                .fecha(LocalDate.now())
                .resumen(resumen)
                .porFinca(porFinca)
                .deudas(deudas)
                .build();
    }

    @Override
    public ReporteFacturacionDto getFacturacion(
            UUID fincaId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            TipoSalida tipo,
            String agruparPor) {

        LocalDateTime startDateTime = fechaInicio.atStartOfDay();
        LocalDateTime endDateTime = fechaFin.atTime(LocalTime.MAX);

        List<Salida> salidas = salidaRepository.findByFechaAndActivo(startDateTime, endDateTime);

        // Filter by finca if provided
        if (fincaId != null) {
            salidas = salidas.stream()
                    .filter(s -> s.getFincaProducto() != null &&
                            s.getFincaProducto().getFinca() != null &&
                            fincaId.equals(s.getFincaProducto().getFinca().getId()))
                    .collect(Collectors.toList());
        }

        // Filter by tipo if provided
        if (tipo != null) {
            salidas = salidas.stream()
                    .filter(s -> tipo.equals(s.getTipo()))
                    .collect(Collectors.toList());
        }

        // Calculate totals
        int totalVales = (int) salidas.stream().filter(s -> TipoSalida.VALE.equals(s.getTipo())).count();
        int totalFacturas = (int) salidas.stream().filter(s -> TipoSalida.FACTURA.equals(s.getTipo())).count();
        double cantidadProductos = salidas.stream()
                .flatMap(s -> s.getItems().stream())
                .mapToDouble(ItemSalida::getCantidad)
                .sum();
        double valorTotal = salidas.stream()
                .flatMap(s -> s.getItems().stream())
                .mapToDouble(item -> item.getCantidad() * (item.getPrecio() != null ? item.getPrecio() : 0))
                .sum();

        ResumenFacturacionDto resumen = ResumenFacturacionDto.builder()
                .totalDocumentos(salidas.size())
                .totalVales(totalVales)
                .totalFacturas(totalFacturas)
                .cantidadProductos(cantidadProductos)
                .valorTotal(Math.round(valorTotal * 100.0) / 100.0)
                .build();

        // Group by agruparPor
        List<DetalleFacturacionDto> detalle = buildDetalle(salidas, agruparPor, valorTotal);

        // Build documentos list
        List<DocumentoFacturacionDto> documentos = salidas.stream()
                .map(s -> {
                    double cantidad = s.getItems().stream().mapToDouble(ItemSalida::getCantidad).sum();
                    double valor = s.getItems().stream()
                            .mapToDouble(item -> item.getCantidad() * (item.getPrecio() != null ? item.getPrecio() : 0))
                            .sum();
                    return DocumentoFacturacionDto.builder()
                            .id(s.getId())
                            .numero(s.getNumero())
                            .fecha(s.getFecha().toLocalDate())
                            .producto(s.getFincaProducto() != null && s.getFincaProducto().getProducto() != null
                                    ? s.getFincaProducto().getProducto().getName() : "N/A")
                            .cantidad(cantidad)
                            .destino(s.getDestino() != null ? s.getDestino().name() : null)
                            .valor(Math.round(valor * 100.0) / 100.0)
                            .build();
                })
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .collect(Collectors.toList());

        Map<String, String> filtros = new HashMap<>();
        filtros.put("finca", fincaId != null ? fincaId.toString() : "TODAS");
        filtros.put("tipo", tipo != null ? tipo.name() : "TODOS");

        return ReporteFacturacionDto.builder()
                .periodo(PeriodoDto.builder().inicio(fechaInicio).fin(fechaFin).build())
                .filtros(filtros)
                .resumen(resumen)
                .detalle(detalle)
                .documentos(documentos)
                .build();
    }

    private List<DetalleFacturacionDto> buildDetalle(List<Salida> salidas, String agruparPor, double valorTotal) {
        List<DetalleFacturacionDto> detalle = new ArrayList<>();

        if ("DESTINO".equalsIgnoreCase(agruparPor)) {
            Map<String, List<Salida>> byDestino = salidas.stream()
                    .collect(Collectors.groupingBy(s -> s.getDestino() != null ? s.getDestino().name() : "SIN_DESTINO"));
            for (Map.Entry<String, List<Salida>> entry : byDestino.entrySet()) {
                detalle.add(buildDetalleItem(null, null, entry.getKey(), entry.getValue(), valorTotal));
            }
        } else if ("TIPO".equalsIgnoreCase(agruparPor)) {
            Map<String, List<Salida>> byTipo = salidas.stream()
                    .collect(Collectors.groupingBy(s -> s.getTipo() != null ? s.getTipo().name() : "SIN_TIPO"));
            for (Map.Entry<String, List<Salida>> entry : byTipo.entrySet()) {
                detalle.add(buildDetalleItem(null, entry.getKey(), null, entry.getValue(), valorTotal));
            }
        } else {
            // Default: by FINCA
            Map<String, List<Salida>> byFinca = salidas.stream()
                    .collect(Collectors.groupingBy(s -> {
                        if (s.getFincaProducto() != null && s.getFincaProducto().getFinca() != null) {
                            return s.getFincaProducto().getFinca().getName();
                        }
                        return "SIN_FINCA";
                    }));
            for (Map.Entry<String, List<Salida>> entry : byFinca.entrySet()) {
                detalle.add(buildDetalleItem(entry.getKey(), null, null, entry.getValue(), valorTotal));
            }
        }

        detalle.sort((a, b) -> Double.compare(b.getValor(), a.getValor()));
        return detalle;
    }

    private DetalleFacturacionDto buildDetalleItem(String finca, String tipo, String destino, List<Salida> salidas, double valorTotal) {
        int docs = salidas.size();
        double cantidad = salidas.stream()
                .flatMap(s -> s.getItems().stream())
                .mapToDouble(ItemSalida::getCantidad)
                .sum();
        double valor = salidas.stream()
                .flatMap(s -> s.getItems().stream())
                .mapToDouble(item -> item.getCantidad() * (item.getPrecio() != null ? item.getPrecio() : 0))
                .sum();
        double porcentaje = valorTotal > 0 ? Math.round((valor / valorTotal) * 10000.0) / 100.0 : 0;

        return DetalleFacturacionDto.builder()
                .finca(finca)
                .tipo(tipo)
                .destino(destino)
                .documentos(docs)
                .cantidad(cantidad)
                .valor(Math.round(valor * 100.0) / 100.0)
                .porcentaje(porcentaje)
                .build();
    }

    @Override
    public ReporteKardexDto getKardexConsolidado(
            UUID fincaId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            List<UUID> productoIds) {

        Finca finca = fincaRepository.findById(fincaId)
                .orElseThrow(() -> new RuntimeException("Finca not found: " + fincaId));

        LocalDateTime startDateTime = fechaInicio.atStartOfDay();
        LocalDateTime endDateTime = fechaFin.atTime(LocalTime.MAX);

        // Get all finca-producto IDs that had movements in the period
        List<UUID> fincaProductoIds = movimientoStockRepository
                .findDistinctFincaProductoIdsByFincaIdAndFechaBetween(fincaId, startDateTime, endDateTime);

        // Filter by productoIds if provided
        List<FincaProducto> fincaProductos;
        if (productoIds != null && !productoIds.isEmpty()) {
            fincaProductos = fincaProductoRepository.findAll().stream()
                    .filter(fp -> fincaId.equals(fp.getFinca().getId()) && productoIds.contains(fp.getProducto().getId()))
                    .collect(Collectors.toList());
        } else {
            fincaProductos = fincaProductoRepository.findAll().stream()
                    .filter(fp -> fincaId.equals(fp.getFinca().getId()) && fincaProductoIds.contains(fp.getId()))
                    .collect(Collectors.toList());
        }

        List<ProductoKardexDto> productos = new ArrayList<>();
        double totalStockInicial = 0.0;
        double totalEntradas = 0.0;
        double totalSalidas = 0.0;
        double totalStockFinal = 0.0;

        for (FincaProducto fp : fincaProductos) {
            List<MovimientoStock> movimientos = movimientoStockRepository
                    .findByFincaProductoIdAndFechaBetween(fp.getId(), startDateTime, endDateTime);

            if (movimientos.isEmpty()) continue;

            // Calculate stock inicial (first movement's stockAnterior)
            double stockInicial = movimientos.get(0).getStockAnterior();
            double stockFinal = movimientos.get(movimientos.size() - 1).getStockNuevo();

            // Group entradas/salidas
            Map<String, Double> entradasPorTipo = new HashMap<>();
            Map<String, Double> salidasPorTipo = new HashMap<>();
            double entradas = 0.0;
            double salidas = 0.0;

            List<MovimientoKardexDto> movimientosDto = new ArrayList<>();
            for (MovimientoStock m : movimientos) {
                String tipoStr = m.getTipo() != null ? m.getTipo().name() : "OTRO";
                double cant = m.getCantidad();

                if (isEntrada(m.getTipo())) {
                    entradas += cant;
                    entradasPorTipo.merge(tipoStr, cant, Double::sum);
                } else {
                    salidas += cant;
                    salidasPorTipo.merge(tipoStr, cant, Double::sum);
                }

                movimientosDto.add(MovimientoKardexDto.builder()
                        .fecha(m.getFecha())
                        .tipo(m.getTipo())
                        .cantidad(cant)
                        .stockResultante(m.getStockNuevo())
                        .referencia(m.getDescripcion())
                        .referenciaId(m.getReferenciaId())
                        .build());
            }

            Producto producto = fp.getProducto();
            productos.add(ProductoKardexDto.builder()
                    .productoId(producto.getId())
                    .productoCode(producto.getCode())
                    .productoName(producto.getName())
                    .unidadMedida(producto.getUnidadMedida() != null ? producto.getUnidadMedida().name() : null)
                    .stockInicial(stockInicial)
                    .entradas(EntradasSalidasDto.builder().total(entradas).porTipo(entradasPorTipo).build())
                    .salidas(EntradasSalidasDto.builder().total(salidas).porTipo(salidasPorTipo).build())
                    .stockFinal(stockFinal)
                    .movimientos(movimientosDto)
                    .build());

            totalStockInicial += stockInicial;
            totalEntradas += entradas;
            totalSalidas += salidas;
            totalStockFinal += stockFinal;
        }

        return ReporteKardexDto.builder()
                .finca(FincaInfoDto.builder()
                        .id(finca.getId())
                        .code(finca.getCode())
                        .name(finca.getName())
                        .build())
                .periodo(PeriodoDto.builder().inicio(fechaInicio).fin(fechaFin).build())
                .productos(productos)
                .totales(TotalesKardexDto.builder()
                        .stockInicialTotal(totalStockInicial)
                        .entradasTotal(totalEntradas)
                        .salidasTotal(totalSalidas)
                        .stockFinalTotal(totalStockFinal)
                        .build())
                .build();
    }

    private boolean isEntrada(TipoMovimientoStock tipo) {
        if (tipo == null) return false;
        return tipo.isEntrada();
    }

    @Override
    public ReporteMovimientosGraficoDto getMovimientosGrafico(
            UUID fincaId,
            UUID productoId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Granularidad granularidad) {

        LocalDateTime startDateTime = fechaInicio.atStartOfDay();
        LocalDateTime endDateTime = fechaFin.atTime(LocalTime.MAX);

        List<MovimientoStock> movimientos;
        if (fincaId != null) {
            movimientos = movimientoStockRepository.findByFincaIdAndFechaBetween(fincaId, startDateTime, endDateTime);
        } else {
            movimientos = movimientoStockRepository.findAll().stream()
                    .filter(m -> !m.getFecha().isBefore(startDateTime) && !m.getFecha().isAfter(endDateTime))
                    .collect(Collectors.toList());
        }

        // Filter by producto if provided
        if (productoId != null) {
            movimientos = movimientos.stream()
                    .filter(m -> productoId.equals(m.getProductoId()))
                    .collect(Collectors.toList());
        }

        // Build time series based on granularity
        List<String> etiquetas = new ArrayList<>();
        Map<String, Double> entradasPorPeriodo = new LinkedHashMap<>();
        Map<String, Double> salidasPorPeriodo = new LinkedHashMap<>();

        DateTimeFormatter formatter;
        switch (granularidad) {
            case SEMANA:
                formatter = DateTimeFormatter.ofPattern("'S'w/yyyy");
                break;
            case MES:
                formatter = DateTimeFormatter.ofPattern("MMM/yyyy");
                break;
            default: // DIA
                formatter = DateTimeFormatter.ofPattern("dd/MM");
        }

        // Initialize periods
        LocalDate current = fechaInicio;
        while (!current.isAfter(fechaFin)) {
            String key = getKey(current, granularidad);
            etiquetas.add(key);
            entradasPorPeriodo.put(key, 0.0);
            salidasPorPeriodo.put(key, 0.0);
            current = advanceDate(current, granularidad);
        }

        // Aggregate movements
        for (MovimientoStock m : movimientos) {
            String key = getKey(m.getFecha().toLocalDate(), granularidad);
            if (isEntrada(m.getTipo())) {
                entradasPorPeriodo.merge(key, m.getCantidad(), Double::sum);
            } else {
                salidasPorPeriodo.merge(key, m.getCantidad(), Double::sum);
            }
        }

        // Remove duplicates from etiquetas while preserving order
        etiquetas = new ArrayList<>(new LinkedHashSet<>(etiquetas));

        List<Double> entradasDatos = etiquetas.stream()
                .map(k -> entradasPorPeriodo.getOrDefault(k, 0.0))
                .collect(Collectors.toList());
        List<Double> salidasDatos = etiquetas.stream()
                .map(k -> salidasPorPeriodo.getOrDefault(k, 0.0))
                .collect(Collectors.toList());

        double totalEntradas = entradasDatos.stream().mapToDouble(Double::doubleValue).sum();
        double totalSalidas = salidasDatos.stream().mapToDouble(Double::doubleValue).sum();

        return ReporteMovimientosGraficoDto.builder()
                .etiquetas(etiquetas)
                .series(Arrays.asList(
                        SerieGraficoDto.builder()
                                .nombre("Entradas")
                                .color("#27ae60")
                                .datos(entradasDatos)
                                .build(),
                        SerieGraficoDto.builder()
                                .nombre("Salidas")
                                .color("#e74c3c")
                                .datos(salidasDatos)
                                .build()
                ))
                .totales(TotalesGraficoDto.builder()
                        .entradas(totalEntradas)
                        .salidas(totalSalidas)
                        .balance(totalEntradas - totalSalidas)
                        .build())
                .build();
    }

    private String getKey(LocalDate date, Granularidad granularidad) {
        switch (granularidad) {
            case SEMANA:
                int week = date.get(WeekFields.ISO.weekOfWeekBasedYear());
                return "S" + week + "/" + date.getYear();
            case MES:
                return date.format(DateTimeFormatter.ofPattern("MMM/yyyy"));
            default:
                return date.format(DateTimeFormatter.ofPattern("dd/MM"));
        }
    }

    private LocalDate advanceDate(LocalDate date, Granularidad granularidad) {
        switch (granularidad) {
            case SEMANA:
                return date.plusWeeks(1);
            case MES:
                return date.plusMonths(1);
            default:
                return date.plusDays(1);
        }
    }

    @Override
    public ResumenPagosDto getResumenPagos(UUID fincaId, LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime startDateTime = fechaInicio.atStartOfDay();
        LocalDateTime endDateTime = fechaFin.atTime(LocalTime.MAX);

        // Fetch payments by date range
        List<PagoDeuda> pagos;
        if (fincaId != null) {
            pagos = pagoDeudaRepository.findByFincaIdAndFechaBetween(fincaId, startDateTime, endDateTime);
        } else {
            pagos = pagoDeudaRepository.findByFechaBetween(startDateTime, endDateTime);
        }

        // Calculate totals by payment method
        double montoEfectivo = pagos.stream()
                .filter(p -> p.getFormaPago() != null && "EFECTIVO".equals(p.getFormaPago().name()))
                .mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0)
                .sum();

        double montoTransferencia = pagos.stream()
                .filter(p -> p.getFormaPago() != null && "TRANSFERENCIA".equals(p.getFormaPago().name()))
                .mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0)
                .sum();

        double montoTotal = montoEfectivo + montoTransferencia;

        double porcentajeEfectivo = montoTotal > 0 ? Math.round((montoEfectivo / montoTotal) * 10000.0) / 100.0 : 0;
        double porcentajeTransferencia = montoTotal > 0 ? Math.round((montoTransferencia / montoTotal) * 10000.0) / 100.0 : 0;

        ResumenPagosTotalesDto resumen = ResumenPagosTotalesDto.builder()
                .totalPagos(pagos.size())
                .montoTotal(Math.round(montoTotal * 100.0) / 100.0)
                .montoEfectivo(Math.round(montoEfectivo * 100.0) / 100.0)
                .montoTransferencia(Math.round(montoTransferencia * 100.0) / 100.0)
                .porcentajeEfectivo(porcentajeEfectivo)
                .porcentajeTransferencia(porcentajeTransferencia)
                .build();

        // Group by payment method for detail
        List<DetallePagosPorMetodoDto> detallePorMetodo = new ArrayList<>();

        Map<String, List<PagoDeuda>> porMetodo = pagos.stream()
                .collect(Collectors.groupingBy(p -> p.getFormaPago() != null ? p.getFormaPago().name() : "SIN_METODO"));

        for (Map.Entry<String, List<PagoDeuda>> entry : porMetodo.entrySet()) {
            double monto = entry.getValue().stream()
                    .mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0)
                    .sum();
            double porcentaje = montoTotal > 0 ? Math.round((monto / montoTotal) * 10000.0) / 100.0 : 0;

            detallePorMetodo.add(DetallePagosPorMetodoDto.builder()
                    .formaPago(entry.getKey())
                    .cantidad(entry.getValue().size())
                    .monto(Math.round(monto * 100.0) / 100.0)
                    .porcentaje(porcentaje)
                    .build());
        }
        detallePorMetodo.sort((a, b) -> Double.compare(b.getMonto(), a.getMonto()));

        // Build documents list
        List<PagoDocumentoDto> documentos = pagos.stream()
                .map(p -> PagoDocumentoDto.builder()
                        .numeroRecibo(p.getNumeroRecibo())
                        .fecha(p.getFecha())
                        .trabajadorNombre(p.getTrabajador() != null ? p.getTrabajador().getNombre() : null)
                        .trabajadorRuc(p.getTrabajador() != null ? p.getTrabajador().getRuc() : null)
                        .fincaNombre(p.getFinca() != null ? p.getFinca().getName() : null)
                        .monto(p.getMonto())
                        .formaPago(p.getFormaPago() != null ? p.getFormaPago().name() : null)
                        .referenciaBancaria(p.getReferenciaBancaria())
                        .build())
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .collect(Collectors.toList());

        Map<String, String> filtros = new HashMap<>();
        filtros.put("finca", fincaId != null ? fincaId.toString() : "TODAS");

        return ResumenPagosDto.builder()
                .periodo(PeriodoDto.builder().inicio(fechaInicio).fin(fechaFin).build())
                .filtros(filtros)
                .resumen(resumen)
                .detallePorMetodo(detallePorMetodo)
                .documentos(documentos)
                .build();
    }

    @Override
    public ResumenVentasDto getResumenVentas(UUID fincaId, LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime startDateTime = fechaInicio.atStartOfDay();
        LocalDateTime endDateTime = fechaFin.atTime(LocalTime.MAX);

        List<Salida> salidas = salidaRepository.findByFechaAndActivo(startDateTime, endDateTime);

        // Filter by finca if provided
        if (fincaId != null) {
            salidas = salidas.stream()
                    .filter(s -> s.getFincaProducto() != null &&
                            s.getFincaProducto().getFinca() != null &&
                            fincaId.equals(s.getFincaProducto().getFinca().getId()))
                    .collect(Collectors.toList());
        }

        // Calculate totals
        double totalItems = salidas.stream()
                .flatMap(s -> s.getItems().stream())
                .mapToDouble(ItemSalida::getCantidad)
                .sum();

        double valorTotal = salidas.stream()
                .flatMap(s -> s.getItems().stream())
                .mapToDouble(item -> item.getCantidad() * (item.getPrecio() != null ? item.getPrecio() : 0))
                .sum();

        // Group by destino and calculate values
        Map<String, List<Salida>> porDestino = salidas.stream()
                .collect(Collectors.groupingBy(s -> s.getDestino() != null ? s.getDestino().name() : "SIN_DESTINO"));

        Map<String, Double> valorPorDestino = new HashMap<>();
        List<DetalleVentasPorDestinoDto> detallePorDestino = new ArrayList<>();

        for (Map.Entry<String, List<Salida>> entry : porDestino.entrySet()) {
            double cantItems = entry.getValue().stream()
                    .flatMap(s -> s.getItems().stream())
                    .mapToDouble(ItemSalida::getCantidad)
                    .sum();
            double valor = entry.getValue().stream()
                    .flatMap(s -> s.getItems().stream())
                    .mapToDouble(item -> item.getCantidad() * (item.getPrecio() != null ? item.getPrecio() : 0))
                    .sum();
            double porcentaje = valorTotal > 0 ? Math.round((valor / valorTotal) * 10000.0) / 100.0 : 0;

            valorPorDestino.put(entry.getKey(), Math.round(valor * 100.0) / 100.0);

            detallePorDestino.add(DetalleVentasPorDestinoDto.builder()
                    .destino(entry.getKey())
                    .cantidadSalidas(entry.getValue().size())
                    .cantidadItems(cantItems)
                    .valor(Math.round(valor * 100.0) / 100.0)
                    .porcentaje(porcentaje)
                    .build());
        }
        detallePorDestino.sort((a, b) -> Double.compare(b.getValor(), a.getValor()));

        ResumenVentasTotalesDto resumen = ResumenVentasTotalesDto.builder()
                .totalSalidas(salidas.size())
                .totalItems(totalItems)
                .valorTotal(Math.round(valorTotal * 100.0) / 100.0)
                .valorPorDestino(valorPorDestino)
                .build();

        // Build documents list
        List<VentaDocumentoDto> documentos = salidas.stream()
                .map(s -> {
                    double cantidad = s.getItems().stream().mapToDouble(ItemSalida::getCantidad).sum();
                    double valor = s.getItems().stream()
                            .mapToDouble(item -> item.getCantidad() * (item.getPrecio() != null ? item.getPrecio() : 0))
                            .sum();
                    return VentaDocumentoDto.builder()
                            .numero(s.getNumero())
                            .fecha(s.getFecha())
                            .tipo(s.getTipo() != null ? s.getTipo().name() : null)
                            .destino(s.getDestino() != null ? s.getDestino().name() : null)
                            .fincaNombre(s.getFincaProducto() != null && s.getFincaProducto().getFinca() != null
                                    ? s.getFincaProducto().getFinca().getName() : null)
                            .productoNombre(s.getFincaProducto() != null && s.getFincaProducto().getProducto() != null
                                    ? s.getFincaProducto().getProducto().getName() : null)
                            .cantidad(cantidad)
                            .valorTotal(Math.round(valor * 100.0) / 100.0)
                            .build();
                })
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .collect(Collectors.toList());

        Map<String, String> filtros = new HashMap<>();
        filtros.put("finca", fincaId != null ? fincaId.toString() : "TODAS");

        return ResumenVentasDto.builder()
                .periodo(PeriodoDto.builder().inicio(fechaInicio).fin(fechaFin).build())
                .filtros(filtros)
                .resumen(resumen)
                .detallePorDestino(detallePorDestino)
                .documentos(documentos)
                .build();
    }
}
