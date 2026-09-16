package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.report.domain.dto.CreateProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.DeleteProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.dto.UpdateProduccionTerminadaResult;
import com.kynsoft.report.domain.services.IFincaProductoService;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import com.kynsoft.report.domain.services.IProduccionTerminadaService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.infrastructure.entity.ProduccionTerminada;
import com.kynsoft.report.infrastructure.repository.command.ProduccionTerminadaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProduccionTerminadaReadDataJPARepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProduccionTerminadaServiceImpl implements IProduccionTerminadaService {

    private final ProduccionTerminadaWriteDataJPARepository repositoryCommand;
    private final ProduccionTerminadaReadDataJPARepository repositoryQuery;
    private final IFincaProductoService fincaProductoService;
    private final ITrabajadorService trabajadorService;
    private final IAlmacenFincaProductoService almacenFincaProductoService;

    public ProduccionTerminadaServiceImpl(
            ProduccionTerminadaWriteDataJPARepository repositoryCommand,
            ProduccionTerminadaReadDataJPARepository repositoryQuery,
            IFincaProductoService fincaProductoService,
            ITrabajadorService trabajadorService,
            IAlmacenFincaProductoService almacenFincaProductoService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.fincaProductoService = fincaProductoService;
        this.trabajadorService = trabajadorService;
        this.almacenFincaProductoService = almacenFincaProductoService;
    }

    @Override
    @Transactional
    public CreateProduccionTerminadaResult create(ProduccionTerminadaDto dto) {
        if (dto.getId() == null) {
            dto.setId(UUID.randomUUID());
        }
        // RN-03: Validar cantidad mayor a cero
        if (dto.getCantidadTerminada() == null || dto.getCantidadTerminada() <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidadTerminada", "La cantidad terminada debe ser mayor a 0.")));
        }

        // RN-01: Validar producto asignado a finca
        FincaProductoDto fincaProducto;
        try {
            fincaProducto = fincaProductoService.obtenerRelacion(dto.getFincaId(), dto.getProductoId());
        } catch (BusinessNotFoundException e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("productoId", "El producto no está asignado a la finca.")));
        }

        // Validar trabajadores existen
        TrabajadorDto trabajadorEntrega = trabajadorService.findById(dto.getTrabajadorEntregaId());
        TrabajadorDto trabajadorRecibe = trabajadorService.findById(dto.getTrabajadorRecibeId());

        // RN-05: Validar trabajadores diferentes
        if (dto.getTrabajadorEntregaId().equals(dto.getTrabajadorRecibeId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("trabajadorRecibeId", "El trabajador que entrega y el que recibe deben ser diferentes.")));
        }

        // RN-04: Validar trabajadores de la misma finca
        if (!dto.getFincaId().equals(trabajadorEntrega.getFincaId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("trabajadorEntregaId", "El trabajador que entrega no pertenece a la finca.")));
        }
        if (!dto.getFincaId().equals(trabajadorRecibe.getFincaId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("trabajadorRecibeId", "El trabajador que recibe no pertenece a la finca.")));
        }

        // Guardar stock anterior
        Double stockAnterior = fincaProducto.getStock() != null ? fincaProducto.getStock() : 0.0;

        // Crear entidad
        ProduccionTerminada entity = new ProduccionTerminada(dto);
        ProduccionTerminada saved = repositoryCommand.save(entity);

        // RN-02: Incrementar stock automáticamente
        fincaProductoService.entradaProduccion(
                dto.getFincaId(),
                dto.getProductoId(),
                dto.getCantidadTerminada(),
                "Producción terminada: " + (dto.getObservaciones() != null ? dto.getObservaciones() : ""),
                saved.getId()
        );

        Double stockNuevo = stockAnterior + dto.getCantidadTerminada();

        return CreateProduccionTerminadaResult.builder()
                .id(saved.getId())
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
                .build();
    }

    @Override
    @Transactional
    public CreateProduccionTerminadaResult createEnAlmacen(UUID almacenId, UUID almacenFincaProductoId,
                                                            ProduccionTerminadaDto dto) {
        if (dto.getId() == null) {
            dto.setId(UUID.randomUUID());
        }
        if (dto.getCantidadTerminada() == null || dto.getCantidadTerminada() <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidadTerminada", "La cantidad terminada debe ser mayor a 0.")));
        }

        AlmacenFincaProductoDto almacenProducto = almacenFincaProductoService.findById(almacenFincaProductoId);
        if (!almacenId.equals(almacenProducto.getAlmacenId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("almacenFincaProductoId", "El producto no pertenece al almacén seleccionado.")));
        }

        FincaProductoDto fincaProducto = fincaProductoService.getById(almacenProducto.getFincaProductoId());
        validarContextoAlmacen(dto, fincaProducto);
        dto.setFincaId(fincaProducto.getFincaId());
        dto.setProductoId(fincaProducto.getProductoId());
        dto.setAlmacenFincaProductoId(almacenFincaProductoId);

        validarTrabajadores(dto);

        Double stockAnterior = almacenProducto.getStock() != null ? almacenProducto.getStock() : 0.0;
        ProduccionTerminada saved = repositoryCommand.save(new ProduccionTerminada(dto));

        // Este método es el único que incrementa el inventario físico y el consolidado
        // de finca. Nunca se debe invocar fincaProductoService.entradaProduccion aquí.
        almacenFincaProductoService.registrarEntradaProduccionTerminada(
                almacenFincaProductoId,
                dto.getCantidadTerminada(),
                saved.getId(),
                descripcionProduccion(dto.getObservaciones()),
                null
        );

        return CreateProduccionTerminadaResult.builder()
                .id(saved.getId())
                .stockAnterior(stockAnterior)
                .stockNuevo(stockAnterior + dto.getCantidadTerminada())
                .build();
    }

    @Override
    @Transactional
    public UpdateProduccionTerminadaResult update(ProduccionTerminadaDto dto) {
        ProduccionTerminada entity = repositoryQuery.findById(dto.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Producción terminada no encontrada."))));

        // Validar que no esté anulada
        if (!entity.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "No se puede modificar una producción anulada.")));
        }

        // RN-03: Validar cantidad mayor a cero
        if (dto.getCantidadTerminada() == null || dto.getCantidadTerminada() <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidadTerminada", "La cantidad terminada debe ser mayor a 0.")));
        }

        // Validar trabajadores existen
        TrabajadorDto trabajadorEntrega = trabajadorService.findById(dto.getTrabajadorEntregaId());
        TrabajadorDto trabajadorRecibe = trabajadorService.findById(dto.getTrabajadorRecibeId());

        // RN-05: Validar trabajadores diferentes
        if (dto.getTrabajadorEntregaId().equals(dto.getTrabajadorRecibeId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("trabajadorRecibeId", "El trabajador que entrega y el que recibe deben ser diferentes.")));
        }

        // RN-04: Validar trabajadores de la misma finca
        if (!entity.getFincaId().equals(trabajadorEntrega.getFincaId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("trabajadorEntregaId", "El trabajador que entrega no pertenece a la finca.")));
        }
        if (!entity.getFincaId().equals(trabajadorRecibe.getFincaId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("trabajadorRecibeId", "El trabajador que recibe no pertenece a la finca.")));
        }

        boolean produccionEnAlmacen = entity.getAlmacenFincaProductoId() != null;
        Double stockActual = produccionEnAlmacen
                ? obtenerStockAlmacen(entity.getAlmacenFincaProductoId())
                : fincaProductoService.obtenerStock(entity.getFincaId(), entity.getProductoId());
        Double cantidadAnterior = entity.getCantidadTerminada();
        Double cantidadNueva = dto.getCantidadTerminada();
        Double ajuste = cantidadNueva - cantidadAnterior;

        // RN-06: Validar que no quede stock negativo si el ajuste es negativo
        if (ajuste < 0 && (stockActual + ajuste) < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidadTerminada", "El ajuste dejaría el stock negativo. Stock actual: " + stockActual)));
        }

        // Actualizar entidad
        if (!produccionEnAlmacen && dto.getProductoId() != null) {
            entity.setProductoId(dto.getProductoId());
        }
        entity.setCantidadTerminada(dto.getCantidadTerminada());
        entity.setTrabajadorEntregaId(dto.getTrabajadorEntregaId());
        entity.setTrabajadorRecibeId(dto.getTrabajadorRecibeId());
        entity.setObservaciones(dto.getObservaciones());

        repositoryCommand.save(entity);

        // RN-06: Ajustar stock según diferencia. La entrada vinculada ajusta almacén
        // y finca desde un único movimiento especializado, sin doble incremento.
        if (Double.compare(ajuste, 0.0) != 0) {
            if (produccionEnAlmacen) {
                almacenFincaProductoService.actualizarEntradaProduccion(
                        entity.getAlmacenFincaProductoId(),
                        cantidadAnterior,
                        cantidadNueva,
                        entity.getId(),
                        "Ajuste por edición de producción terminada"
                );
            } else if (ajuste > 0) {
                // Incrementar stock
                fincaProductoService.entradaProduccion(
                        entity.getFincaId(),
                        entity.getProductoId(),
                        ajuste,
                        "Ajuste por edición de producción terminada",
                        entity.getId()
                );
            } else {
                // Decrementar stock
                fincaProductoService.decrementarStock(
                        entity.getFincaId(),
                        entity.getProductoId(),
                        Math.abs(ajuste),
                        TipoMovimientoStock.AJUSTE_EDICION,
                        entity.getId(),
                        "produccion_terminada"
                );
            }
        }

        Double stockNuevo = stockActual + ajuste;

        return UpdateProduccionTerminadaResult.builder()
                .id(entity.getId())
                .stockAnterior(stockActual)
                .stockNuevo(stockNuevo)
                .ajuste(ajuste)
                .build();
    }

    @Override
    @Transactional
    public DeleteProduccionTerminadaResult delete(UUID id) {
        ProduccionTerminada entity = repositoryQuery.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Producción terminada no encontrada."))));

        // Validar que no esté ya anulada
        if (!entity.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "La producción ya está anulada.")));
        }

        boolean produccionEnAlmacen = entity.getAlmacenFincaProductoId() != null;
        Double stockActual = produccionEnAlmacen
                ? obtenerStockAlmacen(entity.getAlmacenFincaProductoId())
                : fincaProductoService.obtenerStock(entity.getFincaId(), entity.getProductoId());
        Double cantidadARevertir = entity.getCantidadTerminada();

        // RN-07: Validar que no quede stock negativo
        if ((stockActual - cantidadARevertir) < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "La reversión dejaría el stock negativo. Stock actual: " + stockActual + ", cantidad a revertir: " + cantidadARevertir)));
        }

        // Soft delete: marcar como inactivo
        entity.setActivo(false);
        repositoryCommand.save(entity);

        // RN-07: Revertir stock en la misma ubicación donde entró la producción.
        if (produccionEnAlmacen) {
            almacenFincaProductoService.revertirEntradaProduccion(
                    entity.getAlmacenFincaProductoId(),
                    cantidadARevertir,
                    entity.getId(),
                    "Reversión de producción terminada"
            );
        } else {
            fincaProductoService.decrementarStock(
                    entity.getFincaId(),
                    entity.getProductoId(),
                    cantidadARevertir,
                    TipoMovimientoStock.DEVOLUCION,
                    entity.getId(),
                    "produccion_terminada"
            );
        }

        Double stockNuevo = stockActual - cantidadARevertir;

        return DeleteProduccionTerminadaResult.builder()
                .id(entity.getId())
                .stockAnterior(stockActual)
                .stockNuevo(stockNuevo)
                .cantidadRevertida(cantidadARevertir)
                .build();
    }

    @Override
    public ProduccionTerminadaDto findById(UUID id) {
        return repositoryQuery.findByIdWithDetails(id)
                .map(ProduccionTerminada::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Producción terminada no encontrada."))));
    }

    @Override
    public List<ProduccionTerminadaDto> findByProductoId(UUID productoId) {
        return repositoryQuery.findByProductoIdAndActivoTrue(productoId).stream()
                .map(ProduccionTerminada::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProduccionTerminadaDto> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return repositoryQuery.findByFechaBetweenAndActivoTrue(fechaInicio, fechaFin).stream()
                .map(ProduccionTerminada::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProduccionTerminadaDto> findByTrabajadorEntregaId(UUID trabajadorId) {
        return repositoryQuery.findByTrabajadorEntregaIdAndActivoTrue(trabajadorId).stream()
                .map(ProduccionTerminada::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProduccionTerminadaDto> findByTrabajadorRecibeId(UUID trabajadorId) {
        return repositoryQuery.findByTrabajadorRecibeIdAndActivoTrue(trabajadorId).stream()
                .map(ProduccionTerminada::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProduccionTerminadaDto> findByFincaId(UUID fincaId) {
        return repositoryQuery.findByFincaIdAndActivoTrue(fincaId).stream()
                .map(ProduccionTerminada::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProduccionTerminadaDto> findByFincaIdAndFechaBetween(UUID fincaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return repositoryQuery.findByFincaIdAndFechaBetweenAndActivoTrue(fincaId, fechaInicio, fechaFin).stream()
                .map(ProduccionTerminada::toAggregate)
                .collect(Collectors.toList());
    }

    private void validarContextoAlmacen(ProduccionTerminadaDto dto, FincaProductoDto fincaProducto) {
        if (dto.getFincaId() != null && !dto.getFincaId().equals(fincaProducto.getFincaId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaId", "La finca no coincide con el producto del almacén.")));
        }
        if (dto.getProductoId() != null && !dto.getProductoId().equals(fincaProducto.getProductoId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("productoId", "El producto no coincide con el producto del almacén.")));
        }
    }

    private void validarTrabajadores(ProduccionTerminadaDto dto) {
        TrabajadorDto trabajadorEntrega = trabajadorService.findById(dto.getTrabajadorEntregaId());
        TrabajadorDto trabajadorRecibe = trabajadorService.findById(dto.getTrabajadorRecibeId());

        if (dto.getTrabajadorEntregaId().equals(dto.getTrabajadorRecibeId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("trabajadorRecibeId", "El trabajador que entrega y el que recibe deben ser diferentes.")));
        }
        if (!dto.getFincaId().equals(trabajadorEntrega.getFincaId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("trabajadorEntregaId", "El trabajador que entrega no pertenece a la finca.")));
        }
        if (!dto.getFincaId().equals(trabajadorRecibe.getFincaId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("trabajadorRecibeId", "El trabajador que recibe no pertenece a la finca.")));
        }
    }

    private Double obtenerStockAlmacen(UUID almacenFincaProductoId) {
        AlmacenFincaProductoDto almacenProducto = almacenFincaProductoService.findById(almacenFincaProductoId);
        return almacenProducto.getStock() != null ? almacenProducto.getStock() : 0.0;
    }

    private String descripcionProduccion(String observaciones) {
        return "Producción terminada" + (observaciones != null && !observaciones.isBlank()
                ? ": " + observaciones
                : "");
    }
}
