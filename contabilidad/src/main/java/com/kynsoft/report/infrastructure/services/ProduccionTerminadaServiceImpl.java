package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.report.domain.dto.CreateProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.DeleteProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.dto.UpdateProduccionTerminadaResult;
import com.kynsoft.report.domain.services.IFincaProductoService;
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

    public ProduccionTerminadaServiceImpl(
            ProduccionTerminadaWriteDataJPARepository repositoryCommand,
            ProduccionTerminadaReadDataJPARepository repositoryQuery,
            IFincaProductoService fincaProductoService,
            ITrabajadorService trabajadorService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.fincaProductoService = fincaProductoService;
        this.trabajadorService = trabajadorService;
    }

    @Override
    @Transactional
    public CreateProduccionTerminadaResult create(ProduccionTerminadaDto dto) {
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
        Integer stockAnterior = fincaProducto.getStock() != null ? fincaProducto.getStock() : 0;

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

        Integer stockNuevo = stockAnterior + dto.getCantidadTerminada();

        return CreateProduccionTerminadaResult.builder()
                .id(saved.getId())
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
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

        // Obtener stock actual
        Integer stockActual = fincaProductoService.obtenerStock(entity.getFincaId(), entity.getProductoId());
        Integer cantidadAnterior = entity.getCantidadTerminada();
        Integer cantidadNueva = dto.getCantidadTerminada();
        Integer ajuste = cantidadNueva - cantidadAnterior;

        // RN-06: Validar que no quede stock negativo si el ajuste es negativo
        if (ajuste < 0 && (stockActual + ajuste) < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidadTerminada", "El ajuste dejaría el stock negativo. Stock actual: " + stockActual)));
        }

        // Actualizar entidad
        entity.setProductoId(dto.getProductoId());
        entity.setCantidadTerminada(dto.getCantidadTerminada());
        entity.setTrabajadorEntregaId(dto.getTrabajadorEntregaId());
        entity.setTrabajadorRecibeId(dto.getTrabajadorRecibeId());
        entity.setObservaciones(dto.getObservaciones());

        repositoryCommand.save(entity);

        // RN-06: Ajustar stock según diferencia
        if (ajuste != 0) {
            if (ajuste > 0) {
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

        Integer stockNuevo = stockActual + ajuste;

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

        // Obtener stock actual
        Integer stockActual = fincaProductoService.obtenerStock(entity.getFincaId(), entity.getProductoId());
        Integer cantidadARevertir = entity.getCantidadTerminada();

        // RN-07: Validar que no quede stock negativo
        if ((stockActual - cantidadARevertir) < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "La reversión dejaría el stock negativo. Stock actual: " + stockActual + ", cantidad a revertir: " + cantidadARevertir)));
        }

        // Soft delete: marcar como inactivo
        entity.setActivo(false);
        repositoryCommand.save(entity);

        // RN-07: Revertir stock
        fincaProductoService.decrementarStock(
                entity.getFincaId(),
                entity.getProductoId(),
                cantidadARevertir,
                TipoMovimientoStock.DEVOLUCION,
                entity.getId(),
                "produccion_terminada"
        );

        Integer stockNuevo = stockActual - cantidadARevertir;

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
}
