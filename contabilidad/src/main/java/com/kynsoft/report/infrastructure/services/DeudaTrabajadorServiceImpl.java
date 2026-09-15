package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.DeudaTrabajadorResponse;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDetalleDto;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDto;
import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.MovimientoDeudaResult;
import com.kynsoft.report.domain.dto.TipoMovimiento;
import com.kynsoft.report.domain.services.IDeudaTrabajadorDetalleService;
import com.kynsoft.report.domain.services.IDeudaTrabajadorService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DeudaTrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeudaTrabajadorServiceImpl implements IDeudaTrabajadorService {

    private final DeudaTrabajadorWriteDataJPARepository repositoryCommand;
    private final DeudaTrabajadorReadDataJPARepository repositoryQuery;
    private final IDeudaTrabajadorDetalleService detalleService;
    private final ITrabajadorService trabajadorService;

    public DeudaTrabajadorServiceImpl(
            DeudaTrabajadorWriteDataJPARepository repositoryCommand,
            DeudaTrabajadorReadDataJPARepository repositoryQuery,
            IDeudaTrabajadorDetalleService detalleService,
            ITrabajadorService trabajadorService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.detalleService = detalleService;
        this.trabajadorService = trabajadorService;
    }

    @Override
    public UUID create(DeudaTrabajadorDto dto) {
        DeudaTrabajador entity = new DeudaTrabajador(dto);
        DeudaTrabajador saved = repositoryCommand.save(entity);
        return saved.getId();
    }

    @Override
    public void update(DeudaTrabajadorDto dto) {
        repositoryCommand.save(new DeudaTrabajador(dto));
    }

    @Override
    public void delete(UUID id) {
        try {
            repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "El elemento no puede ser eliminado.")));
        }
    }

    @Override
    public DeudaTrabajadorDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(DeudaTrabajador::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Deuda de trabajador no encontrada."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<DeudaTrabajador> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Specification<DeudaTrabajador> combinedSpec = Specification
                .where(specifications)
                .and(TenantSpecification.byFincaViaTrabajador());
        Page<DeudaTrabajador> data = repositoryQuery.findAll(combinedSpec, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<DeudaTrabajador> data) {
        List<DeudaTrabajadorResponse> responses = data.getContent().stream()
                .map(DeudaTrabajador::toAggregate)
                .map(DeudaTrabajadorResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public DeudaTrabajadorDto findByTrabajadorId(UUID trabajadorId) {
        return repositoryQuery.findByTrabajadorId(trabajadorId)
                .map(DeudaTrabajador::toAggregate)
                .orElse(null);
    }

    @Override
    @Transactional
    public void incrementarDeuda(UUID trabajadorId, Double importe) {
        DeudaTrabajador deuda = repositoryQuery.findByTrabajadorId(trabajadorId).orElse(null);

        if (deuda == null) {
            // Crear nueva deuda si no existe
            DeudaTrabajadorDto dto = DeudaTrabajadorDto.builder()
                    .id(UUID.randomUUID())
                    .trabajadorId(trabajadorId)
                    .importe(importe)
                    .build();
            repositoryCommand.save(new DeudaTrabajador(dto));
        } else {
            // Incrementar deuda existente
            deuda.setImporte(deuda.getImporte() + importe);
            repositoryCommand.save(deuda);
        }
    }

    @Override
    @Transactional
    public MovimientoDeudaResult registrarPago(UUID trabajadorId, Double monto, FormaPago formaPago,
                                                String referenciaBancaria, String observaciones) {
        // Validar trabajador existe
        trabajadorService.findById(trabajadorId);

        // Validar monto > 0
        if (monto == null || monto <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("monto", "El monto debe ser mayor a 0.")));
        }

        // Obtener deuda actual
        DeudaTrabajador deuda = repositoryQuery.findByTrabajadorId(trabajadorId).orElse(null);
        Double saldoAnterior = deuda != null ? deuda.getImporte() : 0.0;

        // RN-02: Validar monto <= deuda actual
        if (monto > saldoAnterior) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("monto", "El monto excede la deuda actual. Deuda: " + saldoAnterior)));
        }

        // RN-03: Validar referencia bancaria para transferencia
        if (formaPago == FormaPago.TRANSFERENCIA &&
            (referenciaBancaria == null || referenciaBancaria.trim().isEmpty())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("referenciaBancaria", "Referencia bancaria requerida para transferencia.")));
        }

        // Actualizar saldo
        Double saldoNuevo = saldoAnterior - monto;
        if (deuda != null) {
            deuda.setImporte(saldoNuevo);
            repositoryCommand.save(deuda);
        }

        // Registrar detalle de movimiento
        DeudaTrabajadorDetalleDto detalle = DeudaTrabajadorDetalleDto.builder()
                .id(UUID.randomUUID())
                .trabajadorId(trabajadorId)
                .tipoMovimiento(TipoMovimiento.PAGO)
                .importe(-monto) // Negativo porque reduce la deuda
                .fecha(LocalDateTime.now())
                .formaPago(formaPago)
                .referenciaBancaria(referenciaBancaria)
                .observaciones(observaciones)
                .activo(true)
                .build();
        UUID detalleId = detalleService.create(detalle);

        return MovimientoDeudaResult.builder()
                .id(detalleId)
                .saldoAnterior(saldoAnterior)
                .saldoNuevo(saldoNuevo)
                .build();
    }

    @Override
    @Transactional
    public MovimientoDeudaResult registrarAjuste(UUID trabajadorId, Double monto, String observaciones) {
        // Validar trabajador existe
        trabajadorService.findById(trabajadorId);

        // Validar monto != 0
        if (monto == null || monto == 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("monto", "El monto de ajuste no puede ser 0.")));
        }

        // Validar observaciones no vacías
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("observaciones", "Las observaciones son obligatorias para ajustes.")));
        }

        // Obtener deuda actual
        DeudaTrabajador deuda = repositoryQuery.findByTrabajadorId(trabajadorId).orElse(null);
        Double saldoAnterior = deuda != null ? deuda.getImporte() : 0.0;
        Double saldoNuevo = saldoAnterior + monto;

        // RN-07: Validar no dejar saldo negativo
        if (saldoNuevo < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("monto", "El ajuste dejaría saldo negativo. Saldo actual: " + saldoAnterior)));
        }

        // Actualizar o crear saldo
        if (deuda != null) {
            deuda.setImporte(saldoNuevo);
            repositoryCommand.save(deuda);
        } else if (saldoNuevo > 0) {
            DeudaTrabajadorDto dto = DeudaTrabajadorDto.builder()
                    .id(UUID.randomUUID())
                    .trabajadorId(trabajadorId)
                    .importe(saldoNuevo)
                    .build();
            repositoryCommand.save(new DeudaTrabajador(dto));
        }

        // Registrar detalle de movimiento
        DeudaTrabajadorDetalleDto detalle = DeudaTrabajadorDetalleDto.builder()
                .id(UUID.randomUUID())
                .trabajadorId(trabajadorId)
                .tipoMovimiento(TipoMovimiento.AJUSTE)
                .importe(monto)
                .fecha(LocalDateTime.now())
                .observaciones(observaciones)
                .activo(true)
                .build();
        UUID detalleId = detalleService.create(detalle);

        return MovimientoDeudaResult.builder()
                .id(detalleId)
                .saldoAnterior(saldoAnterior)
                .saldoNuevo(saldoNuevo)
                .build();
    }

    @Override
    @Transactional
    public MovimientoDeudaResult registrarCargaInicial(UUID trabajadorId, Double monto, String observaciones) {
        // Validar trabajador existe
        trabajadorService.findById(trabajadorId);

        // Validar monto > 0
        if (monto == null || monto <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("monto", "El monto debe ser mayor a 0.")));
        }

        // RN-08: Validar observaciones obligatorias
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("observaciones", "Las observaciones son obligatorias para carga inicial.")));
        }

        // Obtener deuda actual
        DeudaTrabajador deuda = repositoryQuery.findByTrabajadorId(trabajadorId).orElse(null);
        Double saldoAnterior = deuda != null ? deuda.getImporte() : 0.0;
        Double saldoNuevo = saldoAnterior + monto;

        // Actualizar o crear saldo
        if (deuda != null) {
            deuda.setImporte(saldoNuevo);
            repositoryCommand.save(deuda);
        } else {
            DeudaTrabajadorDto dto = DeudaTrabajadorDto.builder()
                    .id(UUID.randomUUID())
                    .trabajadorId(trabajadorId)
                    .importe(saldoNuevo)
                    .build();
            repositoryCommand.save(new DeudaTrabajador(dto));
        }

        // Registrar detalle de movimiento
        DeudaTrabajadorDetalleDto detalle = DeudaTrabajadorDetalleDto.builder()
                .id(UUID.randomUUID())
                .trabajadorId(trabajadorId)
                .tipoMovimiento(TipoMovimiento.CARGA_INICIAL)
                .importe(monto)
                .fecha(LocalDateTime.now())
                .observaciones(observaciones)
                .activo(true)
                .build();
        UUID detalleId = detalleService.create(detalle);

        return MovimientoDeudaResult.builder()
                .id(detalleId)
                .saldoAnterior(saldoAnterior)
                .saldoNuevo(saldoNuevo)
                .build();
    }
}
