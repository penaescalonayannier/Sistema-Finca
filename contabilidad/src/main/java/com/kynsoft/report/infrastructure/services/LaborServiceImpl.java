package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.LaborResponse;
import com.kynsoft.report.domain.dto.LaborDto;
import com.kynsoft.report.domain.services.ILaborService;
import com.kynsoft.report.infrastructure.entity.Labor;
import com.kynsoft.report.infrastructure.repository.command.LaborWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LaborReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TipoCultivoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CargoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CuentaContableReadDataJPARepository;
import com.kynsoft.report.infrastructure.entity.CuentaContable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@Transactional
public class LaborServiceImpl implements ILaborService {

    private final LaborWriteDataJPARepository repositoryCommand;
    private final LaborReadDataJPARepository repositoryQuery;
    private final TipoCultivoReadDataJPARepository tipoCultivoRepository;
    private final CargoReadDataJPARepository cargoRepository;
    private final CuentaContableReadDataJPARepository cuentaContableRepository;

    public LaborServiceImpl(LaborWriteDataJPARepository repositoryCommand,
                            LaborReadDataJPARepository repositoryQuery,
                            TipoCultivoReadDataJPARepository tipoCultivoRepository,
                            CargoReadDataJPARepository cargoRepository,
                            CuentaContableReadDataJPARepository cuentaContableRepository) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.tipoCultivoRepository = tipoCultivoRepository;
        this.cargoRepository = cargoRepository;
        this.cuentaContableRepository = cuentaContableRepository;
    }

    @Override
    public void create(LaborDto object) {
        validar(object);
        repositoryCommand.save(new Labor(object));
    }

    @Override
    public void update(LaborDto object) {
        validar(object);
        repositoryCommand.save(new Labor(object));
    }

    @Override
    public void delete(UUID id) {
        try {
            repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "Element cannot be deleted as it has a related element.")));
        }
    }

    @Override
    public LaborDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Labor::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Labor not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Labor> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Labor> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Labor> data) {
        List<LaborResponse> responses = data.getContent().stream()
                .map(Labor::toAggregate)
                .map(LaborResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    private void validar(LaborDto labor) {
        if (labor.getCode() == null || labor.getCode().isBlank()) {
            throw new IllegalArgumentException("El código de la labor es obligatorio.");
        }
        if (labor.getName() == null || labor.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la labor es obligatorio.");
        }
        validarNoNegativo(labor.getNormaRendimiento(), "La norma o rendimiento");
        validarNoNegativo(labor.getIndiceConsumo(), "El índice de consumo");
        if (labor.getIndiceConsumo() != null && labor.getIndiceConsumo().compareTo(BigDecimal.ZERO) > 0
                && (labor.getNivelActividad() == null || labor.getNivelActividad().isBlank())) {
            throw new IllegalArgumentException("Indique el nivel de actividad para definir el índice permisible.");
        }
        validarNoNegativo(labor.getDotacionEstandar(), "La dotación estándar");
        validarNoNegativo(labor.getDiasEstimados(), "Los días estimados");
        validarNoNegativo(labor.getHorasEstimadas(), "Las horas estimadas");
        if (labor.getTipoCultivoId() != null && !tipoCultivoRepository.existsById(labor.getTipoCultivoId())) {
            throw new IllegalArgumentException("El tipo de cultivo seleccionado no existe.");
        }
        if (labor.getCodigoCentroCosto() != null && !labor.getCodigoCentroCosto().isBlank()) {
            CuentaContable cuenta = cuentaContableRepository.findByCodigoAndActivoTrue(labor.getCodigoCentroCosto())
                    .orElseThrow(() -> new IllegalArgumentException("El centro de costo seleccionado no existe o está inactivo."));
            if (!Boolean.TRUE.equals(cuenta.getEsCentroCosto())) {
                throw new IllegalArgumentException("La cuenta seleccionada no está definida como centro de costo.");
            }
        }
        if (labor.getCargosRequeridos() != null) {
            for (UUID cargoId : labor.getCargosRequeridos()) {
                if (cargoId == null || !cargoRepository.existsById(cargoId)) {
                    throw new IllegalArgumentException("Uno de los cargos requeridos no existe.");
                }
            }
        }
    }

    private void validarNoNegativo(BigDecimal valor, String etiqueta) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(etiqueta + " no puede ser negativa.");
        }
    }
}
