package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.TrabajadorDiaResponse;
import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.ITrabajadorDiaService;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.command.TrabajadorDiaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorDiaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrabajadorDiaServiceImpl implements ITrabajadorDiaService {

    private final TrabajadorDiaWriteDataJPARepository repositoryCommand;
    private final TrabajadorDiaReadDataJPARepository repositoryQuery;
    private final DiaTrabajoReadDataJPARepository diaTrabajoRepository;
    private final TrabajadorReadDataJPARepository trabajadorRepository;
    private final AuditoriaTransaccionalService auditoria;

    public TrabajadorDiaServiceImpl(
            TrabajadorDiaWriteDataJPARepository repositoryCommand,
            TrabajadorDiaReadDataJPARepository repositoryQuery,
            DiaTrabajoReadDataJPARepository diaTrabajoRepository,
            TrabajadorReadDataJPARepository trabajadorRepository,
            AuditoriaTransaccionalService auditoria) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.diaTrabajoRepository = diaTrabajoRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.auditoria = auditoria;
    }

    @Override
    public void create(TrabajadorDiaDto object) {
        // Validar que el día exista
        DiaTrabajo diaTrabajo = diaTrabajoRepository.findById(object.getDiaTrabajoId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("diaTrabajoId", "Día de trabajo no encontrado."))));

        // Validar que el trabajador exista
        Trabajador trabajador = trabajadorRepository.findById(object.getTrabajadorId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("trabajadorId", "Trabajador no encontrado."))));

        UUID fincaReporte = diaTrabajo.getReporte().getFincaId();
        validarEscritura(fincaReporte);
        if (fincaReporte != null && !fincaReporte.equals(trabajador.getFincaId())) {
            throw new IllegalArgumentException("El trabajador debe pertenecer a la misma finca del reporte");
        }

        // Validar que no exista ya el trabajador en este día
        repositoryQuery.findByDiaTrabajoIdAndTrabajadorId(object.getDiaTrabajoId(), object.getTrabajadorId())
                .ifPresent(td -> {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("trabajadorId", "El trabajador ya está asignado a este día.")));
                });

        TrabajadorDia td = new TrabajadorDia();
        td.setId(object.getId());
        td.setDiaTrabajo(diaTrabajo);
        td.setTrabajador(trabajador);
        td.setHoras(ValidacionParteTrabajo.horas(object.getHoras()));
        td.setNorma(ValidacionParteTrabajo.normaOpcional(object.getNorma()));

        TrabajadorDia creado = repositoryCommand.save(td);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE, "TRABAJADOR_DIA", creado.getId(),
                "Registrada jornada de trabajador", null, resumen(creado));
    }

    @Override
    public void update(TrabajadorDiaDto object) {
        TrabajadorDia td = repositoryQuery.findById(object.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "TrabajadorDia no encontrado."))));
        validarEscritura(td.getDiaTrabajo().getReporte().getFincaId());

        Map<String, Object> anterior = resumen(td);
        td.setHoras(ValidacionParteTrabajo.horas(object.getHoras()));
        td.setNorma(ValidacionParteTrabajo.normaOpcional(object.getNorma()));

        TrabajadorDia actualizado = repositoryCommand.save(td);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE, "TRABAJADOR_DIA", actualizado.getId(),
                "Actualizada jornada de trabajador", anterior, resumen(actualizado));
    }

    @Override
    public void delete(UUID id) {
        try {
            TrabajadorDia td = repositoryQuery.findById(id)
                    .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("id", "TrabajadorDia no encontrado."))));
            validarEscritura(td.getDiaTrabajo().getReporte().getFincaId());
            repositoryCommand.deleteById(id);
            auditoria.registrarDespuesDeConfirmar(TipoAccion.DELETE, "TRABAJADOR_DIA", id,
                    "Eliminada jornada de trabajador", resumen(td), null);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "No se puede eliminar el trabajador del día.")));
        }
    }

    @Override
    public TrabajadorDiaDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(td -> {
                    validarLectura(td.getDiaTrabajo().getReporte().getFincaId());
                    return td.toAggregate();
                })
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "TrabajadorDia no encontrado."))));
    }

    @Override
    public List<TrabajadorDiaDto> findByDiaTrabajoId(UUID diaTrabajoId) {
        validarLecturaDia(diaTrabajoId);
        List<TrabajadorDia> trabajadores = repositoryQuery.findByDiaTrabajoIdWithDetails(diaTrabajoId);
        return trabajadores.stream()
                .map(TrabajadorDia::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TrabajadorDia> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        org.springframework.data.jpa.domain.Specification<TrabajadorDia> tenantSpec = (root, query, cb) -> {
            UUID fincaId = TenantContext.getEffectiveFincaId();
            return fincaId == null ? cb.conjunction()
                    : cb.equal(root.join("diaTrabajo").join("reporte").get("fincaId"), fincaId);
        };
        Page<TrabajadorDia> data = repositoryQuery.findAll(
                org.springframework.data.jpa.domain.Specification.where(specifications).and(tenantSpec), pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<TrabajadorDia> data) {
        List<TrabajadorDiaResponse> responses = data.getContent().stream()
                .map(TrabajadorDia::toAggregate)
                .map(TrabajadorDiaResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    private Map<String, Object> resumen(TrabajadorDia jornada) {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("diaTrabajoId", jornada.getDiaTrabajo() == null ? null : jornada.getDiaTrabajo().getId());
        datos.put("trabajadorId", jornada.getTrabajador() == null ? null : jornada.getTrabajador().getId());
        datos.put("horas", jornada.getHoras());
        datos.put("norma", jornada.getNorma());
        return datos;
    }

    private void validarLecturaDia(UUID diaTrabajoId) {
        DiaTrabajo dia = diaTrabajoRepository.findById(diaTrabajoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("diaTrabajoId", "Día de trabajo no encontrado."))));
        validarLectura(dia.getReporte().getFincaId());
    }

    private void validarLectura(UUID fincaId) {
        if (fincaId != null && TenantContext.get() != null) {
            TenantValidator.validateReadAccess(fincaId);
        }
    }

    private void validarEscritura(UUID fincaId) {
        if (fincaId != null && TenantContext.get() != null) {
            TenantValidator.validateWriteAccess(fincaId);
        }
    }
}
