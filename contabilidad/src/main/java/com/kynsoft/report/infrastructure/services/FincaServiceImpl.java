package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.FincaResponse;
import com.kynsoft.report.domain.dto.DeleteFincaResponse;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.dto.FincaResumenDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.IFincaService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.repository.command.FincaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FincaServiceImpl implements IFincaService {

    private final FincaWriteDataJPARepository repositoryCommand;
    private final FincaReadDataJPARepository repositoryQuery;
    private final TrabajadorReadDataJPARepository trabajadorRepository;
    private final FincaProductoReadDataJPARepository fincaProductoRepository;
    private final ITrabajadorService trabajadorService;

    public FincaServiceImpl(FincaWriteDataJPARepository repositoryCommand,
                            FincaReadDataJPARepository repositoryQuery,
                            TrabajadorReadDataJPARepository trabajadorRepository,
                            FincaProductoReadDataJPARepository fincaProductoRepository,
                            ITrabajadorService trabajadorService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.trabajadorRepository = trabajadorRepository;
        this.fincaProductoRepository = fincaProductoRepository;
        this.trabajadorService = trabajadorService;
    }

    @Override
    public void create(FincaDto object) {
        // Validar que el código no exista
        if (object.getCode() != null) {
            repositoryQuery.findByCode(object.getCode())
                .ifPresent(finca -> {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("code", "Finca con código " + object.getCode() + " ya existe.")));
                });
        }
        repositoryCommand.save(new Finca(object));
    }

    @Override
    public void update(FincaDto object) {
        // Verificar que la finca exista
        repositoryQuery.findById(object.getId())
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Finca no encontrada."))));

        repositoryCommand.save(new Finca(object));
    }

    @Override
    public DeleteFincaResponse delete(UUID id) {
        Finca finca = repositoryQuery.findById(id)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Finca no encontrada."))));

        // Verificar que no esté ya inactiva
        if (!finca.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("activo", "La finca ya está inactiva.")));
        }

        // Generar advertencias
        List<String> advertencias = new ArrayList<>();
        Long cantidadTrabajadores = countTrabajadoresByFincaId(id);
        Long cantidadProductos = countProductosByFincaId(id);

        if (cantidadTrabajadores > 0) {
            advertencias.add("La finca tiene " + cantidadTrabajadores + " trabajadores asignados");
        }
        if (cantidadProductos > 0) {
            advertencias.add("La finca tiene " + cantidadProductos + " productos asignados");
        }

        // Soft delete: marcar como inactivo
        finca.setActivo(false);
        repositoryCommand.save(finca);

        return DeleteFincaResponse.builder()
                .id(id)
                .advertencias(advertencias)
                .build();
    }

    @Override
    public void reactivar(UUID id) {
        Finca finca = repositoryQuery.findById(id)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Finca no encontrada."))));

        // Verificar que esté inactiva
        if (finca.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("activo", "La finca ya está activa.")));
        }

        finca.setActivo(true);
        repositoryCommand.save(finca);
    }

    @Override
    public void asignarResponsable(UUID fincaId, UUID responsableId) {
        Finca finca = repositoryQuery.findById(fincaId)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Finca no encontrada."))));

        // Validar que el trabajador exista
        TrabajadorDto trabajador = trabajadorService.findById(responsableId);

        // Validar que el trabajador pertenezca a la finca (RN-05)
        if (trabajador.getFincaId() != null && !trabajador.getFincaId().equals(fincaId)) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("responsableId", "El responsable debe pertenecer a la finca.")));
        }

        finca.setResponsableId(responsableId);
        repositoryCommand.save(finca);
    }

    @Override
    public FincaDto findById(UUID id) {
        Finca finca = repositoryQuery.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Finca no encontrada."))));

        FincaDto dto = finca.toAggregate();
        // Agregar campos derivados
        dto.setCantidadTrabajadores(countTrabajadoresByFincaId(id));
        dto.setCantidadProductos(countProductosByFincaId(id));
        return dto;
    }

    @Override
    public FincaDto findByCode(String code) {
        Finca finca = repositoryQuery.findByCode(code)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("code", "Finca con código " + code + " no encontrada."))));

        FincaDto dto = finca.toAggregate();
        dto.setCantidadTrabajadores(countTrabajadoresByFincaId(finca.getId()));
        dto.setCantidadProductos(countProductosByFincaId(finca.getId()));
        return dto;
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        // Construir especificación base con filtros del usuario
        GenericSpecificationsBuilder<Finca> specifications = new GenericSpecificationsBuilder<>(filterCriteria);

        // Agregar filtro de activos por defecto
        Specification<Finca> activoSpec = (root, query, cb) -> cb.equal(root.get("activo"), true);
        Specification<Finca> combinedSpec = Specification.where(specifications).and(activoSpec);

        Page<Finca> data = repositoryQuery.findAll(combinedSpec, pageable);
        return createPaginatedResponse(data);
    }

    @Override
    public Long countTrabajadoresByFincaId(UUID fincaId) {
        return trabajadorRepository.countByFincaIdAndActivoTrue(fincaId);
    }

    @Override
    public Long countProductosByFincaId(UUID fincaId) {
        return fincaProductoRepository.countByFincaIdAndActivoTrue(fincaId);
    }

    @Override
    public FincaResumenDto getResumen(UUID fincaId) {
        Finca finca = repositoryQuery.findById(fincaId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Finca no encontrada."))));

        FincaResumenDto.FincaEstadisticasDto estadisticas = FincaResumenDto.FincaEstadisticasDto.builder()
                .totalTrabajadores(countTrabajadoresByFincaId(fincaId))
                .totalProductos(countProductosByFincaId(fincaId))
                .totalProduccionMes(0L) // TODO: Implementar cuando exista ProduccionTerminada
                .totalSalidasMes(0L) // TODO: Implementar cuando exista Salida
                .stockTotalValorizado(0.0) // TODO: Implementar cálculo de valorización
                .build();

        return FincaResumenDto.builder()
                .id(finca.getId())
                .code(finca.getCode())
                .name(finca.getName())
                .area(finca.getArea())
                .responsableNombre(finca.getResponsable() != null ? finca.getResponsable().getNombre() : null)
                .estadisticas(estadisticas)
                .build();
    }

    private PaginatedResponse createPaginatedResponse(Page<Finca> data) {
        List<FincaResponse> responses = data.getContent().stream()
                .map(finca -> {
                    FincaDto dto = finca.toAggregate();
                    dto.setCantidadTrabajadores(countTrabajadoresByFincaId(finca.getId()));
                    dto.setCantidadProductos(countProductosByFincaId(finca.getId()));
                    return new FincaResponse(dto);
                })
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}