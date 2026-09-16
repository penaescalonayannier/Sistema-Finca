package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.FincaProductoResponse;
import com.kynsoft.report.domain.dto.AlertaStockDto;
import com.kynsoft.report.domain.dto.EstadoStock;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.dto.ResumenAlertasDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import lombok.extern.slf4j.Slf4j;
import com.kynsoft.report.domain.services.IFincaProductoService;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.Producto;
import com.kynsoft.report.infrastructure.repository.command.FincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
@Slf4j
public class FincaProductoServiceImpl implements IFincaProductoService {

    private final FincaProductoWriteDataJPARepository repositoryCommand;
    private final FincaProductoReadDataJPARepository repositoryQuery;
    private final FincaReadDataJPARepository fincaRepository;
    private final ProductoReadDataJPARepository productoRepository;
    private final IMovimientoStockService movimientoStockService;

    public FincaProductoServiceImpl(
            FincaProductoWriteDataJPARepository repositoryCommand,
            FincaProductoReadDataJPARepository repositoryQuery,
            FincaReadDataJPARepository fincaRepository,
            ProductoReadDataJPARepository productoRepository,
            IMovimientoStockService movimientoStockService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.fincaRepository = fincaRepository;
        this.productoRepository = productoRepository;
        this.movimientoStockService = movimientoStockService;
    }

    @Override
    public UUID asignarProductoAFinca(UUID fincaId, UUID productoId, Double stock, Double stockMinimo) {
        // Validar que la finca exista
        Finca finca = fincaRepository.findById(fincaId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("fincaId", "Finca no encontrada."))));

        // Validar que el producto exista
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("productoId", "Producto no encontrado."))));

        // Verificar si ya existe la relación (activa o inactiva)
        var existingOpt = repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId);
        if (existingOpt.isPresent()) {
            FincaProducto existing = existingOpt.get();
            if (existing.getActivo()) {
                // Ya está activa: error
                throw new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("productoId", "Producto ya asignado a esta finca.")));
            } else {
                // Reactivar relación existente (RN-05)
                Double stockAnterior = existing.getStock();
                existing.setActivo(true);
                existing.setStock(stock);
                existing.setStockMinimo(stockMinimo != null ? stockMinimo : 0.0);
                repositoryCommand.save(existing);

                // Registrar movimiento de reactivación
                movimientoStockService.registrarMovimiento(
                        existing.getId(),
                        fincaId,
                        productoId,
                        TipoMovimientoStock.STOCK_INICIAL,
                        stock,
                        stockAnterior,
                        stock,
                        null,
                        null,
                        "Reactivación de producto en finca"
                );
                return existing.getId();
            }
        }

        // Crear nueva relación
        FincaProducto fincaProducto = new FincaProducto();
        fincaProducto.setId(UUID.randomUUID());
        fincaProducto.setFinca(finca);
        fincaProducto.setProducto(producto);
        fincaProducto.setStock(stock);
        fincaProducto.setStockMinimo(stockMinimo != null ? stockMinimo : 0.0);
        fincaProducto.setActivo(true);

        repositoryCommand.save(fincaProducto);

        // Registrar movimiento de stock inicial
        if (stock > 0) {
            movimientoStockService.registrarMovimiento(
                    fincaProducto.getId(),
                    fincaId,
                    productoId,
                    TipoMovimientoStock.STOCK_INICIAL,
                        stock,
                        0.0,
                    stock,
                    null,
                    null,
                    "Asignación inicial de producto a finca"
            );
        }
        return fincaProducto.getId();
    }

    @Override
    public void actualizarConfiguracion(UUID id, Double stockMinimo) {
        FincaProducto fincaProducto = repositoryQuery.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "Relación no encontrada."))));

        fincaProducto.setStockMinimo(stockMinimo != null ? stockMinimo : 0.0);
        repositoryCommand.save(fincaProducto);
    }

    @Override
    public FincaProductoDto getById(UUID id) {
        FincaProducto fp = repositoryQuery.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "Relación no encontrada."))));
        return toDtoWithDetails(fp);
    }

    @Override
    public void actualizarStock(UUID fincaId, UUID productoId, Double stock) {
        FincaProducto fincaProducto = repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("fincaId", "Relationship not found."))));

        Double stockAnterior = fincaProducto.getStock();
        Double diferencia = stock - stockAnterior;

        fincaProducto.setStock(stock);
        repositoryCommand.save(fincaProducto);

        // Registrar movimiento de ajuste manual
        movimientoStockService.registrarMovimiento(
                fincaProducto.getId(),
                fincaId,
                productoId,
                TipoMovimientoStock.AJUSTE_MANUAL,
                diferencia,
                stockAnterior,
                stock,
                null,
                null,
                "Ajuste manual de stock"
        );
    }

    @Override
    public void removerProductoDeFinca(UUID fincaId, UUID productoId) {
        log.info("Intentando remover producto {} de finca {}", productoId, fincaId);

        FincaProducto fincaProducto = repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId)
                .orElseThrow(() -> {
                    log.error("Relación no encontrada: fincaId={}, productoId={}", fincaId, productoId);
                    return new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("relacion",
                                    String.format("Relación no encontrada entre finca %s y producto %s", fincaId, productoId))));
                });

        // Validar que el producto no esté ya inactivo
        if (!fincaProducto.getActivo()) {
            log.warn("Producto ya removido: fincaId={}, productoId={}", fincaId, productoId);
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("activo", "El producto ya fue removido de esta finca.")));
        }

        // RN-04: Solo se puede remover si el stock es 0
        if (fincaProducto.getStock() > 0) {
            log.warn("No se puede remover con stock > 0: stock={}", fincaProducto.getStock());
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("stock", "No se puede remover: el stock actual es " + fincaProducto.getStock() + ". Debe ser 0.")));
        }

        // Soft delete: marcar como inactivo
        fincaProducto.setActivo(false);
        repositoryCommand.save(fincaProducto);
        log.info("Producto {} removido exitosamente de finca {}", productoId, fincaId);
    }

    @Override
    public void removerTodosProductosDeFinca(UUID fincaId) {
        // Verificar que la finca exista
        fincaRepository.findById(fincaId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("fincaId", "Finca not found."))));

        // Soft delete: marcar todos como inactivos
        List<FincaProducto> productos = repositoryQuery.findByFincaId(fincaId);
        for (FincaProducto fp : productos) {
            fp.setActivo(false);
        }
        repositoryCommand.saveAll(productos);
    }

    @Override
    public List<FincaProductoDto> obtenerProductosDeFinca(UUID fincaId) {
        return repositoryQuery.findWithDetailsByFincaId(fincaId)
                .stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<FincaProductoDto> obtenerFincasDeProducto(UUID productoId) {
        return repositoryQuery.findWithDetailsByProductoId(productoId)
                .stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public FincaProductoDto obtenerRelacion(UUID fincaId, UUID productoId) {
        return repositoryQuery.findWithDetailsByFincaIdAndProductoId(fincaId, productoId)
                .map(this::toDtoWithDetails)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("fincaId", "Relationship not found."))));
    }

    @Override
    public Double obtenerStock(UUID fincaId, UUID productoId) {
        return repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId)
                .map(FincaProducto::getStock)
                .orElse(0.0);
    }

    @Override
    public void entradaProduccion(UUID fincaId, UUID productoId, Double cantidad, String descripcion) {
        entradaProduccionInternal(fincaId, productoId, cantidad, descripcion, null, null);
    }

    @Override
    public void entradaProduccion(UUID fincaId, UUID productoId, Double cantidad, String descripcion, String centroCosto) {
        entradaProduccionInternal(fincaId, productoId, cantidad, descripcion, null, centroCosto);
    }

    @Override
    public void entradaProduccion(UUID fincaId, UUID productoId, Double cantidad, String descripcion, UUID referenciaId) {
        entradaProduccionInternal(fincaId, productoId, cantidad, descripcion, referenciaId, null);
    }

    private void entradaProduccionInternal(UUID fincaId, UUID productoId, Double cantidad, String descripcion, UUID referenciaId, String centroCosto) {
        if (cantidad <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "La cantidad debe ser mayor a 0.")));
        }

        FincaProducto fincaProducto = repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaId", "El producto no está asignado a esta finca."))));

        Double stockAnterior = fincaProducto.getStock();
        Double nuevoStock = stockAnterior + cantidad;
        fincaProducto.setStock(nuevoStock);
        repositoryCommand.save(fincaProducto);

        // Registrar movimiento de entrada de producción con centro de costo
        movimientoStockService.registrarMovimiento(
                fincaProducto.getId(),
                fincaId,
                productoId,
                TipoMovimientoStock.ENTRADA_PRODUCCION,
                cantidad,
                stockAnterior,
                nuevoStock,
                referenciaId,
                referenciaId != null ? "produccion_terminada" : null,
                descripcion != null ? descripcion : "Entrada de producción",
                centroCosto
        );
    }

    @Override
    public void entradaFactura(UUID id, Integer cantidad, String numeroFactura, String observaciones) {
        // RN-06: Validar número de factura
        if (numeroFactura == null || numeroFactura.trim().isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("numeroFactura", "Número de factura requerido.")));
        }

        if (cantidad <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "La cantidad debe ser mayor a 0.")));
        }

        FincaProducto fincaProducto = repositoryQuery.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Relación no encontrada."))));

        Double stockAnterior = fincaProducto.getStock();
        Double nuevoStock = stockAnterior + cantidad;
        fincaProducto.setStock(nuevoStock);
        repositoryCommand.save(fincaProducto);

        // Registrar movimiento de entrada por factura
        String descripcion = "Factura: " + numeroFactura;
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            descripcion += " - " + observaciones;
        }
        movimientoStockService.registrarMovimiento(
                fincaProducto.getId(),
                fincaProducto.getFinca().getId(),
                fincaProducto.getProducto().getId(),
                TipoMovimientoStock.ENTRADA_FACTURA,
                cantidad.doubleValue(),
                stockAnterior,
                nuevoStock,
                null,
                null,
                descripcion
        );
    }

    @Override
    public void entradaConduce(UUID id, Integer cantidad, String observaciones) {
        if (cantidad <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "La cantidad debe ser mayor a 0.")));
        }

        FincaProducto fincaProducto = repositoryQuery.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Relación no encontrada."))));

        Double stockAnterior = fincaProducto.getStock();
        Double nuevoStock = stockAnterior + cantidad;
        fincaProducto.setStock(nuevoStock);
        repositoryCommand.save(fincaProducto);

        // Registrar movimiento de entrada por conduce
        movimientoStockService.registrarMovimiento(
                fincaProducto.getId(),
                fincaProducto.getFinca().getId(),
                fincaProducto.getProducto().getId(),
                TipoMovimientoStock.ENTRADA_CONDUCE,
                cantidad.doubleValue(),
                stockAnterior,
                nuevoStock,
                null,
                null,
                observaciones != null ? observaciones : "Entrada por conduce"
        );
    }

    @Override
    public void ajusteManual(UUID id, Double cantidad, String observaciones) {
        // Validar observaciones obligatorias
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("observaciones", "Observaciones requeridas para ajustes manuales.")));
        }

        if (cantidad == 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "La cantidad no puede ser 0.")));
        }

        FincaProducto fincaProducto = repositoryQuery.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Relación no encontrada."))));

        Double stockAnterior = fincaProducto.getStock();
        Double nuevoStock = stockAnterior + cantidad;

        // RN-02: Validar que el stock no quede negativo
        if (nuevoStock < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "El ajuste dejaría stock negativo. Stock actual: " + stockAnterior)));
        }

        fincaProducto.setStock(nuevoStock);
        repositoryCommand.save(fincaProducto);

        // Registrar movimiento de ajuste manual
        movimientoStockService.registrarMovimiento(
                fincaProducto.getId(),
                fincaProducto.getFinca().getId(),
                fincaProducto.getProducto().getId(),
                TipoMovimientoStock.AJUSTE_MANUAL,
                cantidad,
                stockAnterior,
                nuevoStock,
                null,
                null,
                observaciones
        );
    }

    @Override
    public void decrementarStock(UUID fincaId, UUID productoId, Double cantidad) {
        decrementarStock(fincaId, productoId, cantidad, TipoMovimientoStock.AJUSTE_EDICION, null, null);
    }

    @Override
    public void decrementarStock(UUID fincaId, UUID productoId, Double cantidad,
                                  TipoMovimientoStock tipo, UUID referenciaId, String referenciaTabla) {
        if (cantidad <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "La cantidad debe ser mayor a 0.")));
        }

        FincaProducto fincaProducto = repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaId", "El producto no está asignado a esta finca."))));

        Double stockAnterior = fincaProducto.getStock();
        Double nuevoStock = stockAnterior - cantidad;
        if (nuevoStock < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("stock", "No hay suficiente stock para decrementar. Stock actual: " + stockAnterior)));
        }
        fincaProducto.setStock(nuevoStock);
        repositoryCommand.save(fincaProducto);

        // Registrar movimiento de decremento
        movimientoStockService.registrarMovimiento(
                fincaProducto.getId(),
                fincaId,
                productoId,
                tipo,
                -cantidad,
                stockAnterior,
                nuevoStock,
                referenciaId,
                referenciaTabla,
                "Decremento de stock"
        );
    }

    private FincaProductoDto toDtoWithDetails(FincaProducto fp) {
        return FincaProductoDto.builder()
                .id(fp.getId())
                .fincaId(fp.getFinca().getId())
                .fincaCode(fp.getFinca().getCode())
                .fincaName(fp.getFinca().getName())
                .productoId(fp.getProducto().getId())
                .productoCode(fp.getProducto().getCode())
                .productoName(fp.getProducto().getName())
                .productoPrice(fp.getProducto().getPrice())
                .productoTipo(fp.getProducto().getTipoProducto())
                .stock(fp.getStock())
                .stockMinimo(fp.getStockMinimo())
                .stockMaximo(fp.getStockMaximo())
                .estadoStock(fp.getEstadoStock())
                .activo(fp.getActivo())
                .build();
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        // Construir especificación base con filtros del usuario
        GenericSpecificationsBuilder<FincaProducto> specifications = new GenericSpecificationsBuilder<>(filterCriteria);

        // Agregar filtro de activos por defecto y filtro de tenant
        org.springframework.data.jpa.domain.Specification<FincaProducto> activoSpec = (root, query, cb) -> cb.equal(root.get("activo"), true);
        org.springframework.data.jpa.domain.Specification<FincaProducto> combinedSpec = org.springframework.data.jpa.domain.Specification
                .where(specifications)
                .and(activoSpec)
                .and(TenantSpecification.byFincaRelation());

        Page<FincaProducto> data = repositoryQuery.findAll(combinedSpec, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<FincaProducto> data) {
        List<FincaProductoResponse> responses = data.getContent().stream()
                .map(fp -> {
                    FincaProductoResponse response = new FincaProductoResponse();
                    response.setId(fp.getId());
                    response.setFincaId(fp.getFinca().getId());
                    response.setFincaCode(fp.getFinca().getCode());
                    response.setFincaName(fp.getFinca().getName());
                    response.setProductoId(fp.getProducto().getId());
                    response.setProductoCode(fp.getProducto().getCode());
                    response.setProductoName(fp.getProducto().getName());
                    response.setProductoPrice(fp.getProducto().getPrice());
                    response.setProductoTipo(fp.getProducto().getTipoProducto());
                    response.setStock(fp.getStock());
                    response.setStockMinimo(fp.getStockMinimo());
                    response.setActivo(fp.getActivo());
                    // Calcular alerta de stock bajo
                    boolean alerta = fp.getStockMinimo() != null && fp.getStockMinimo() > 0
                            && fp.getStock() != null && fp.getStock() <= fp.getStockMinimo();
                    response.setAlertaStockBajo(alerta);
                    return response;
                })
                .collect(Collectors.toList());

        return new PaginatedResponse(
                responses,
                data.getTotalPages(),
                data.getNumberOfElements(),
                data.getTotalElements(),
                data.getSize(),
                data.getNumber()
        );
    }

    @Override
    public PaginatedResponse getAlertasStockBajo(Pageable pageable) {
        // Buscar productos activos donde stock <= stockMinimo y stockMinimo > 0
        org.springframework.data.jpa.domain.Specification<FincaProducto> spec = (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("activo"), true),
                        cb.greaterThan(root.get("stockMinimo"), 0),
                        cb.lessThanOrEqualTo(root.get("stock"), root.get("stockMinimo"))
                );

        org.springframework.data.jpa.domain.Specification<FincaProducto> combinedSpec = org.springframework.data.jpa.domain.Specification
                .where(spec)
                .and(TenantSpecification.byFincaRelation());

        Page<FincaProducto> data = repositoryQuery.findAll(combinedSpec, pageable);
        return createPaginatedResponse(data);
    }

    @Override
    public ResumenAlertasDto getResumenAlertas(UUID fincaId, EstadoStock estado, int limit) {
        // Get all active products (optionally filtered by finca or tenant)
        List<FincaProducto> productos;

        // Use explicit fincaId if provided, otherwise use tenant's fincaId
        UUID effectiveFincaId = fincaId != null ? fincaId : TenantContext.getEffectiveFincaId();

        if (effectiveFincaId != null) {
            productos = repositoryQuery.findWithDetailsByFincaId(effectiveFincaId).stream()
                    .filter(FincaProducto::getActivo)
                    .collect(Collectors.toList());
        } else {
            // ADMIN without selection sees all
            org.springframework.data.jpa.domain.Specification<FincaProducto> spec = (root, query, cb) ->
                    cb.equal(root.get("activo"), true);
            productos = repositoryQuery.findAll(spec);
        }

        // Count by state
        int criticos = 0, bajos = 0, normales = 0, exceso = 0;
        List<AlertaStockDto> alertas = new java.util.ArrayList<>();

        for (FincaProducto fp : productos) {
            EstadoStock estadoFp = fp.getEstadoStock();
            switch (estadoFp) {
                case CRITICO -> criticos++;
                case BAJO -> bajos++;
                case NORMAL -> normales++;
                case EXCESO -> exceso++;
            }

            // Only add to alertas if matches filter (or no filter) and is CRITICO or BAJO
            if ((estado == null || estado == estadoFp) &&
                (estadoFp == EstadoStock.CRITICO || estadoFp == EstadoStock.BAJO)) {

                alertas.add(AlertaStockDto.builder()
                        .fincaProductoId(fp.getId())
                        .fincaCode(fp.getFinca().getCode())
                        .fincaName(fp.getFinca().getName())
                        .productoCode(fp.getProducto().getCode())
                        .productoName(fp.getProducto().getName())
                        .unidadMedida(fp.getProducto().getUnidadMedida() != null ?
                                fp.getProducto().getUnidadMedida().name() : "UND")
                        .stockActual(fp.getStock())
                        .stockMinimo(fp.getStockMinimo())
                        .deficit(fp.getStockMinimo() - fp.getStock())
                        .estado(estadoFp)
                        .build());
            }
        }

        // Sort: CRITICO first, then by deficit descending
        alertas.sort((a, b) -> {
            if (a.getEstado() == EstadoStock.CRITICO && b.getEstado() != EstadoStock.CRITICO) return -1;
            if (a.getEstado() != EstadoStock.CRITICO && b.getEstado() == EstadoStock.CRITICO) return 1;
            return b.getDeficit().compareTo(a.getDeficit());
        });

        // Limit results
        if (alertas.size() > limit) {
            alertas = alertas.subList(0, limit);
        }

        return ResumenAlertasDto.builder()
                .totalProductos(productos.size())
                .productosCriticos(criticos)
                .productosBajos(bajos)
                .productosNormales(normales)
                .productosExceso(exceso)
                .alertas(alertas)
                .build();
    }

    @Override
    public FincaProductoDto actualizarStockMinMax(UUID id, Double stockMinimo, Double stockMaximo) {
        FincaProducto fincaProducto = repositoryQuery.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Relación no encontrada."))));

        if (stockMinimo != null) {
            fincaProducto.setStockMinimo(stockMinimo);
        }
        if (stockMaximo != null) {
            fincaProducto.setStockMaximo(stockMaximo);
        }
        repositoryCommand.save(fincaProducto);

        log.info("Actualizado stock min/max para FincaProducto {}: min={}, max={}",
                id, fincaProducto.getStockMinimo(), fincaProducto.getStockMaximo());

        return toDtoWithDetails(fincaProducto);
    }

}
