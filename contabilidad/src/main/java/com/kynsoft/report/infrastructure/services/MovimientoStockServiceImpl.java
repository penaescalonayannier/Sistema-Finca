package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.domain.dto.BalanceProductoDto;
import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.KardexDto;
import com.kynsoft.report.domain.dto.MovimientoStockDto;
import com.kynsoft.report.domain.dto.ResumenMovimientosDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.dto.reportes.ReporteMovimientosConsolidadoDto;
import com.kynsoft.report.domain.services.IContabilizacionAutomaticaService;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.infrastructure.entity.Almacen;
import com.kynsoft.report.infrastructure.entity.AlmacenFincaProducto;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.MovimientoStock;
import com.kynsoft.report.infrastructure.entity.Producto;
import com.kynsoft.report.infrastructure.repository.command.AlmacenFincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.FincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.MovimientoStockWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenFincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoStockReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ItemSalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.entity.Salida;
import com.kynsoft.report.infrastructure.entity.ItemSalida;
import com.kynsoft.report.infrastructure.security.TenantSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Slf4j
public class MovimientoStockServiceImpl implements IMovimientoStockService {

    private final MovimientoStockWriteDataJPARepository repositoryCommand;
    private final MovimientoStockReadDataJPARepository repositoryQuery;
    private final FincaReadDataJPARepository fincaRepository;
    private final ProductoReadDataJPARepository productoRepository;
    private final FincaProductoReadDataJPARepository fincaProductoRepository;
    private final AlmacenReadDataJPARepository almacenRepository;
    private final FincaProductoWriteDataJPARepository fincaProductoWriteRepository;
    private final AlmacenFincaProductoReadDataJPARepository almacenFincaProductoRepository;
    private final AlmacenFincaProductoWriteDataJPARepository almacenFincaProductoWriteRepository;
    private final SalidaReadDataJPARepository salidaRepository;
    private final ItemSalidaReadDataJPARepository itemSalidaRepository;
    private final IContabilizacionAutomaticaService contabilizacionService;

    public MovimientoStockServiceImpl(
            MovimientoStockWriteDataJPARepository repositoryCommand,
            MovimientoStockReadDataJPARepository repositoryQuery,
            FincaReadDataJPARepository fincaRepository,
            ProductoReadDataJPARepository productoRepository,
            FincaProductoReadDataJPARepository fincaProductoRepository,
            FincaProductoWriteDataJPARepository fincaProductoWriteRepository,
            AlmacenReadDataJPARepository almacenRepository,
            AlmacenFincaProductoReadDataJPARepository almacenFincaProductoRepository,
            AlmacenFincaProductoWriteDataJPARepository almacenFincaProductoWriteRepository,
            SalidaReadDataJPARepository salidaRepository,
            ItemSalidaReadDataJPARepository itemSalidaRepository,
            @Lazy IContabilizacionAutomaticaService contabilizacionService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.fincaRepository = fincaRepository;
        this.productoRepository = productoRepository;
        this.fincaProductoRepository = fincaProductoRepository;
        this.fincaProductoWriteRepository = fincaProductoWriteRepository;
        this.almacenRepository = almacenRepository;
        this.almacenFincaProductoRepository = almacenFincaProductoRepository;
        this.almacenFincaProductoWriteRepository = almacenFincaProductoWriteRepository;
        this.salidaRepository = salidaRepository;
        this.itemSalidaRepository = itemSalidaRepository;
        this.contabilizacionService = contabilizacionService;
    }

    @Override
    public void registrar(MovimientoStockDto dto) {
        MovimientoStock entity = new MovimientoStock(dto);
        MovimientoStock saved = repositoryCommand.save(entity);

        // Generate automatic accounting entry
        // This happens internally - users don't see or interact with it
        try {
            contabilizacionService.generarAsientoDesdeMovimiento(saved.toAggregate());
        } catch (Exception e) {
            // Log but don't fail the physical movement if accounting fails
            // Accounting entries can be regenerated later if needed
            log.warn("Could not generate accounting entry for movement {}: {}",
                    dto.getId(), e.getMessage());
        }
    }

    @Override
    public void registrarMovimiento(UUID fincaProductoId, UUID fincaId, UUID productoId,
                                     TipoMovimientoStock tipo, Integer cantidad,
                                     Integer stockAnterior, Integer stockNuevo,
                                     UUID referenciaId, String referenciaTabla, String descripcion) {
        registrarMovimiento(fincaProductoId, fincaId, productoId, tipo, cantidad,
                stockAnterior, stockNuevo, referenciaId, referenciaTabla, descripcion, null);
    }

    @Override
    public void registrarMovimiento(UUID fincaProductoId, UUID fincaId, UUID productoId,
                                     TipoMovimientoStock tipo, Integer cantidad,
                                     Integer stockAnterior, Integer stockNuevo,
                                     UUID referenciaId, String referenciaTabla, String descripcion,
                                     String centroCosto) {
        MovimientoStockDto dto = MovimientoStockDto.builder()
                .id(UUID.randomUUID())
                .fincaProductoId(fincaProductoId)
                .fincaId(fincaId)
                .productoId(productoId)
                .tipo(tipo)
                .cantidad(cantidad)
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
                .referenciaId(referenciaId)
                .referenciaTabla(referenciaTabla)
                .descripcion(descripcion)
                .centroCosto(centroCosto)
                .fecha(LocalDateTime.now())
                .build();

        registrar(dto);
    }

    @Override
    public List<MovimientoStockDto> findByFincaProductoId(UUID fincaProductoId) {
        return repositoryQuery.findByFincaProductoId(fincaProductoId)
                .stream()
                .map(MovimientoStock::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoStockDto> findByFincaId(UUID fincaId) {
        return repositoryQuery.findByFincaId(fincaId)
                .stream()
                .map(MovimientoStock::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoStockDto> findByProductoId(UUID productoId) {
        return repositoryQuery.findByProductoId(productoId)
                .stream()
                .map(MovimientoStock::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoStockDto> findByReferencia(UUID referenciaId, String referenciaTabla) {
        return repositoryQuery.findByReferencia(referenciaId, referenciaTabla)
                .stream()
                .map(MovimientoStock::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoStockDto> findByFincaIdAndFechaBetween(UUID fincaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return repositoryQuery.findByFincaIdAndFechaBetween(fincaId, fechaInicio, fechaFin)
                .stream()
                .map(MovimientoStock::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<MovimientoStock> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Specification<MovimientoStock> combinedSpec = Specification
                .where(specifications)
                .and(TenantSpecification.byFincaDirecta());
        Page<MovimientoStock> data = repositoryQuery.findAll(combinedSpec, pageable);

        // Obtener IDs únicos de fincas y productos
        List<UUID> fincaIds = data.getContent().stream()
                .map(MovimientoStock::getFincaId)
                .distinct()
                .collect(Collectors.toList());
        List<UUID> productoIds = data.getContent().stream()
                .map(MovimientoStock::getProductoId)
                .distinct()
                .collect(Collectors.toList());

        // Cargar fincas y productos en batch
        Map<UUID, String> fincaNombres = fincaRepository.findAllById(fincaIds).stream()
                .collect(Collectors.toMap(Finca::getId, Finca::getName));
        Map<UUID, String> productoNombres = productoRepository.findAllById(productoIds).stream()
                .collect(Collectors.toMap(Producto::getId, Producto::getName));

        // Mapear con nombres
        List<MovimientoStockDto> responses = data.getContent().stream()
                .map(m -> {
                    MovimientoStockDto dto = m.toAggregate();
                    dto.setFincaNombre(fincaNombres.get(m.getFincaId()));
                    dto.setProductoNombre(productoNombres.get(m.getProductoId()));
                    return dto;
                })
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public MovimientoStockDto crearAjuste(UUID almacenId, UUID fincaProductoId,
                                           TipoMovimientoStock tipo, Integer cantidad,
                                           String observaciones) {
        // Validar tipo de movimiento
        if (tipo != TipoMovimientoStock.ENTRADA_AJUSTE && tipo != TipoMovimientoStock.SALIDA_AJUSTE) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("tipo", "Tipo de movimiento inválido. Solo se permite ENTRADA_AJUSTE o SALIDA_AJUSTE.")));
        }

        // Validar cantidad > 0
        if (cantidad == null || cantidad <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "La cantidad debe ser mayor a 0.")));
        }

        // RN-04: Validar observaciones obligatorias para ajustes
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("observaciones", "Observaciones obligatorias para ajustes.")));
        }

        FincaProducto fincaProducto = fincaProductoRepository.findByIdWithDetails(fincaProductoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaProductoId", "Producto de finca no encontrado."))));

        AlmacenFincaProducto almacenProducto = almacenFincaProductoRepository
                .findByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("almacenId", "El producto no está disponible en el almacén seleccionado."))));

        Integer stockFincaAnterior = fincaProducto.getStock() != null ? fincaProducto.getStock() : 0;
        Integer stockAlmacenAnterior = almacenProducto.getStock() != null ? almacenProducto.getStock() : 0;
        int variacion = tipo == TipoMovimientoStock.ENTRADA_AJUSTE ? cantidad : -cantidad;
        int stockFincaNuevo = stockFincaAnterior + variacion;
        int stockAlmacenNuevo = stockAlmacenAnterior + variacion;

        if (stockFincaNuevo < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "El ajuste dejaría el stock de la finca en negativo. Stock actual: " + stockFincaAnterior)));
        }
        if (stockAlmacenNuevo < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "El ajuste dejaría el stock del almacén en negativo. Stock actual: " + stockAlmacenAnterior)));
        }

        fincaProducto.setStock(stockFincaNuevo);
        almacenProducto.setStock(stockAlmacenNuevo);
        fincaProductoWriteRepository.save(fincaProducto);
        almacenFincaProductoWriteRepository.save(almacenProducto);

        // Registrar un único movimiento trazable y permitir la contabilización automática por regla.
        MovimientoStockDto dto = MovimientoStockDto.builder()
                .id(UUID.randomUUID())
                .fincaProductoId(fincaProductoId)
                .fincaId(fincaProducto.getFinca().getId())
                .productoId(fincaProducto.getProducto().getId())
                .almacenId(almacenId)
                .tipo(tipo)
                .cantidad(cantidad)
                .stockAnterior(stockAlmacenAnterior)
                .stockNuevo(stockAlmacenNuevo)
                .observaciones(observaciones)
                .fecha(LocalDateTime.now())
                .build();

        registrar(dto);
        return dto;
    }

    @Override
    public MovimientoStockDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(MovimientoStock::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Movimiento de stock no encontrado."))));
    }

    @Override
    public List<MovimientoStockDto> findByAlmacenId(UUID almacenId) {
        return repositoryQuery.findByAlmacenId(almacenId)
                .stream()
                .map(MovimientoStock::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoStockDto> findByAlmacenIdAndFechaBetween(UUID almacenId,
                                                                    LocalDateTime fechaInicio,
                                                                    LocalDateTime fechaFin) {
        return repositoryQuery.findByAlmacenIdAndFechaBetween(almacenId, fechaInicio, fechaFin)
                .stream()
                .map(MovimientoStock::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoStockDto> findByTipo(TipoMovimientoStock tipo) {
        return repositoryQuery.findByTipo(tipo)
                .stream()
                .map(MovimientoStock::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoStockDto> findByTipoAndFincaIdAndFechaBetween(TipoMovimientoStock tipo,
                                                                         UUID fincaId,
                                                                         LocalDateTime fechaInicio,
                                                                         LocalDateTime fechaFin) {
        return repositoryQuery.findByTipoAndFincaIdAndFechaBetween(tipo, fincaId, fechaInicio, fechaFin)
                .stream()
                .map(MovimientoStock::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public ResumenMovimientosDto getResumen(UUID fincaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<MovimientoStock> movimientos = repositoryQuery.findByFincaIdAndFechaBetween(fincaId, fechaInicio, fechaFin);

        Map<String, Long> entradas = new HashMap<>();
        Map<String, Long> salidas = new HashMap<>();
        long totalEntradas = 0;
        long totalSalidas = 0;

        for (MovimientoStock m : movimientos) {
            TipoMovimientoStock tipo = m.getTipo();
            long cantidad = m.getCantidad();

            if (tipo.isEntrada()) {
                entradas.merge(tipo.name(), cantidad, Long::sum);
                totalEntradas += cantidad;
            } else if (tipo.isSalida()) {
                salidas.merge(tipo.name(), cantidad, Long::sum);
                totalSalidas += cantidad;
            }
        }

        entradas.put("total", totalEntradas);
        salidas.put("total", totalSalidas);

        return ResumenMovimientosDto.builder()
                .periodo(ResumenMovimientosDto.PeriodoDto.builder()
                        .fechaInicio(fechaInicio.toLocalDate())
                        .fechaFin(fechaFin.toLocalDate())
                        .build())
                .entradas(entradas)
                .salidas(salidas)
                .balance(totalEntradas - totalSalidas)
                .totalMovimientos((long) movimientos.size())
                .build();
    }

    @Override
    public List<BalanceProductoDto> getBalanceProducto(UUID fincaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Obtener todos los fincaProductoIds con movimientos en el período
        List<UUID> fincaProductoIds = repositoryQuery.findDistinctFincaProductoIdsByFincaIdAndFechaBetween(
                fincaId, fechaInicio, fechaFin);

        List<BalanceProductoDto> result = new ArrayList<>();

        for (UUID fincaProductoId : fincaProductoIds) {
            List<MovimientoStock> movimientos = repositoryQuery.findByFincaProductoIdAndFechaBetween(
                    fincaProductoId, fechaInicio, fechaFin);

            if (movimientos.isEmpty()) continue;

            // Obtener info del producto
            FincaProducto fp = fincaProductoRepository.findById(fincaProductoId).orElse(null);
            if (fp == null) continue;

            Producto producto = productoRepository.findById(fp.getProducto().getId()).orElse(null);
            String productoCode = producto != null ? producto.getCode() : "";
            String productoName = producto != null ? producto.getName() : "";

            // Calcular totales
            long totalEntradas = 0;
            long totalSalidas = 0;
            Integer stockInicial = movimientos.get(0).getStockAnterior();

            for (MovimientoStock m : movimientos) {
                if (m.getTipo().isEntrada()) {
                    totalEntradas += m.getCantidad();
                } else if (m.getTipo().isSalida()) {
                    totalSalidas += m.getCantidad();
                }
            }

            Integer stockFinal = stockInicial + (int) totalEntradas - (int) totalSalidas;

            result.add(BalanceProductoDto.builder()
                    .fincaProductoId(fincaProductoId)
                    .productoCode(productoCode)
                    .productoName(productoName)
                    .stockInicial(stockInicial)
                    .totalEntradas(totalEntradas)
                    .totalSalidas(totalSalidas)
                    .stockFinal(stockFinal)
                    .cantidadMovimientos((long) movimientos.size())
                    .build());
        }

        return result;
    }

    @Override
    public KardexDto getKardex(UUID fincaProductoId, UUID almacenId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Obtener movimientos
        List<MovimientoStock> movimientos;
        if (almacenId != null) {
            movimientos = repositoryQuery.findByFincaProductoIdAndAlmacenIdAndFechaBetween(
                    fincaProductoId, almacenId, fechaInicio, fechaFin);
        } else {
            movimientos = repositoryQuery.findByFincaProductoIdAndFechaBetween(
                    fincaProductoId, fechaInicio, fechaFin);
        }

        // Obtener info del producto
        FincaProducto fp = fincaProductoRepository.findById(fincaProductoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaProductoId", "FincaProducto no encontrado."))));

        Producto producto = productoRepository.findById(fp.getProducto().getId()).orElse(null);

        KardexDto.ProductoInfoDto productoInfo = KardexDto.ProductoInfoDto.builder()
                .fincaProductoId(fincaProductoId)
                .productoCode(producto != null ? producto.getCode() : "")
                .productoName(producto != null ? producto.getName() : "")
                .build();

        KardexDto.AlmacenInfoDto almacenInfo = null;
        if (almacenId != null) {
            Almacen almacen = almacenRepository.findById(almacenId).orElse(null);
            if (almacen != null) {
                almacenInfo = KardexDto.AlmacenInfoDto.builder()
                        .almacenId(almacenId)
                        .almacenNombre(almacen.getNombre())
                        .build();
            }
        }

        // Calcular kardex
        List<KardexDto.MovimientoKardexDto> movimientosKardex = new ArrayList<>();
        Integer stockInicial = movimientos.isEmpty() ? 0 : movimientos.get(0).getStockAnterior();
        Integer saldoActual = stockInicial;
        long totalEntradas = 0;
        long totalSalidas = 0;

        for (MovimientoStock m : movimientos) {
            Integer entrada = 0;
            Integer salida = 0;

            if (m.getTipo().isEntrada()) {
                entrada = m.getCantidad();
                totalEntradas += entrada;
                saldoActual += entrada;
            } else if (m.getTipo().isSalida()) {
                salida = m.getCantidad();
                totalSalidas += salida;
                saldoActual -= salida;
            }

            movimientosKardex.add(KardexDto.MovimientoKardexDto.builder()
                    .fecha(m.getFecha())
                    .tipoMovimiento(m.getTipo())
                    .entrada(entrada)
                    .salida(salida)
                    .saldo(saldoActual)
                    .observaciones(m.getObservaciones())
                    .build());
        }

        return KardexDto.builder()
                .producto(productoInfo)
                .almacen(almacenInfo)
                .stockInicial(stockInicial)
                .movimientos(movimientosKardex)
                .stockFinal(saldoActual)
                .totalEntradas(totalEntradas)
                .totalSalidas(totalSalidas)
                .build();
    }

    @Override
    public ReporteMovimientosConsolidadoDto getConsolidadoMovimientos(LocalDate fechaInicio, LocalDate fechaFin, UUID fincaId) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        // 1. Obtener movimientos de entrada
        List<MovimientoStock> movimientos;
        if (fincaId != null) {
            movimientos = repositoryQuery.findByFincaIdAndFechaBetween(fincaId, inicio, fin);
        } else {
            movimientos = repositoryQuery.findByFechaBetween(inicio, fin);
        }

        // Filtrar solo entradas
        List<MovimientoStock> entradas = movimientos.stream()
                .filter(m -> m.getTipo().isEntrada())
                .collect(Collectors.toList());

        // 2. Agrupar entradas por producto
        Map<UUID, List<MovimientoStock>> entradasPorProducto = entradas.stream()
                .collect(Collectors.groupingBy(MovimientoStock::getProductoId));

        // Cargar info de productos
        List<UUID> productoIds = new ArrayList<>(entradasPorProducto.keySet());
        Map<UUID, Producto> productosMap = productoRepository.findAllById(productoIds).stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        // Crear lista de entradas por producto
        List<ReporteMovimientosConsolidadoDto.EntradaPorProducto> entradasDto = new ArrayList<>();
        int totalEntradas = 0;

        for (Map.Entry<UUID, List<MovimientoStock>> entry : entradasPorProducto.entrySet()) {
            UUID productoId = entry.getKey();
            List<MovimientoStock> movsProd = entry.getValue();
            Producto producto = productosMap.get(productoId);

            int cantidadTotal = movsProd.stream().mapToInt(MovimientoStock::getCantidad).sum();
            totalEntradas += cantidadTotal;

            // Agrupar por tipo de entrada
            Map<TipoMovimientoStock, Integer> porTipo = movsProd.stream()
                    .collect(Collectors.groupingBy(
                            MovimientoStock::getTipo,
                            Collectors.summingInt(MovimientoStock::getCantidad)));

            List<ReporteMovimientosConsolidadoDto.EntradaDetalle> detalles = porTipo.entrySet().stream()
                    .map(e -> ReporteMovimientosConsolidadoDto.EntradaDetalle.builder()
                            .tipo(e.getKey())
                            .cantidad(e.getValue())
                            .descripcion(e.getKey().name().replace("_", " "))
                            .build())
                    .collect(Collectors.toList());

            entradasDto.add(ReporteMovimientosConsolidadoDto.EntradaPorProducto.builder()
                    .productoCode(producto != null ? producto.getCode() : "N/A")
                    .productoName(producto != null ? producto.getName() : "Producto no encontrado")
                    .unidadMedida(producto != null && producto.getUnidadMedida() != null
                            ? producto.getUnidadMedida().name() : "UND")
                    .cantidadTotal(cantidadTotal)
                    .detalles(detalles)
                    .build());
        }

        // Ordenar por cantidad total descendente
        entradasDto.sort((a, b) -> b.getCantidadTotal().compareTo(a.getCantidadTotal()));

        // 3. Obtener salidas agrupadas por destino
        List<Salida> salidas;
        if (fincaId != null) {
            salidas = salidaRepository.findByFincaIdAndFechaBetween(fincaId, inicio, fin);
        } else {
            salidas = salidaRepository.findByFechaBetween(inicio, fin);
        }

        // Agrupar por destino
        Map<DestinoSalida, List<Salida>> salidasPorDestino = salidas.stream()
                .collect(Collectors.groupingBy(Salida::getDestino));

        List<ReporteMovimientosConsolidadoDto.SalidaPorDestino> salidasDto = new ArrayList<>();
        int totalSalidas = 0;

        for (Map.Entry<DestinoSalida, List<Salida>> entry : salidasPorDestino.entrySet()) {
            DestinoSalida destino = entry.getKey();
            List<Salida> salidasDestino = entry.getValue();

            // Obtener todos los items de estas salidas
            List<UUID> salidaIds = salidasDestino.stream().map(Salida::getId).collect(Collectors.toList());
            List<ItemSalida> items = itemSalidaRepository.findBySalidaIdIn(salidaIds);

            int cantidadTotal = items.stream().mapToInt(ItemSalida::getCantidad).sum();
            double valorTotal = items.stream().mapToDouble(i -> i.getCantidad() * i.getPrecio()).sum();
            totalSalidas += cantidadTotal;

            // Agrupar por producto
            Map<UUID, List<Salida>> salidasPorProducto = salidasDestino.stream()
                    .collect(Collectors.groupingBy(Salida::getFincaProductoId));

            // Pre-cargar todos los FincaProducto con sus Producto para evitar LazyInitializationException
            List<UUID> fpIds = new ArrayList<>(salidasPorProducto.keySet());
            Map<UUID, FincaProducto> fincaProductoMap = fincaProductoRepository.findByIdInWithDetails(fpIds).stream()
                    .collect(Collectors.toMap(FincaProducto::getId, fp -> fp));

            List<ReporteMovimientosConsolidadoDto.SalidaProductoDetalle> productosDetalle = new ArrayList<>();
            for (Map.Entry<UUID, List<Salida>> prodEntry : salidasPorProducto.entrySet()) {
                UUID fpId = prodEntry.getKey();
                FincaProducto fp = fincaProductoMap.get(fpId);

                List<UUID> salidaIdsProd = prodEntry.getValue().stream().map(Salida::getId).collect(Collectors.toList());
                List<ItemSalida> itemsProd = items.stream()
                        .filter(i -> salidaIdsProd.contains(i.getSalidaId()))
                        .collect(Collectors.toList());

                int cantProd = itemsProd.stream().mapToInt(ItemSalida::getCantidad).sum();
                double valorProd = itemsProd.stream().mapToDouble(i -> i.getCantidad() * i.getPrecio()).sum();
                double precioPromedio = cantProd > 0 ? valorProd / cantProd : 0;

                productosDetalle.add(ReporteMovimientosConsolidadoDto.SalidaProductoDetalle.builder()
                        .productoCode(fp != null && fp.getProducto() != null ? fp.getProducto().getCode() : "N/A")
                        .productoName(fp != null && fp.getProducto() != null ? fp.getProducto().getName() : "N/A")
                        .cantidad(cantProd)
                        .precio(precioPromedio)
                        .valorTotal(valorProd)
                        .build());
            }

            salidasDto.add(ReporteMovimientosConsolidadoDto.SalidaPorDestino.builder()
                    .destino(destino)
                    .destinoNombre(getDestinoNombre(destino))
                    .cantidadTotal(cantidadTotal)
                    .valorTotal(valorTotal)
                    .productos(productosDetalle)
                    .build());
        }

        // Ordenar por cantidad total descendente
        salidasDto.sort((a, b) -> b.getCantidadTotal().compareTo(a.getCantidadTotal()));

        // 4. Resumen de entradas por tipo
        Map<TipoMovimientoStock, Integer> entradasPorTipo = entradas.stream()
                .collect(Collectors.groupingBy(
                        MovimientoStock::getTipo,
                        Collectors.summingInt(MovimientoStock::getCantidad)));

        return ReporteMovimientosConsolidadoDto.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .totalEntradas(totalEntradas)
                .totalSalidas(totalSalidas)
                .entradasPorProducto(entradasDto)
                .salidasPorDestino(salidasDto)
                .entradasPorTipo(entradasPorTipo)
                .build();
    }

    private String getDestinoNombre(DestinoSalida destino) {
        return switch (destino) {
            case TRABAJADORES -> "Trabajadores";
            case COMEDOR -> "Comedor";
            case VENTA_ESTADO -> "Venta al Estado";
            case POBLACION -> "Población";
            case INSUMO -> "Insumos";
            case OTROS -> "Otros";
        };
    }
}
