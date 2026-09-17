package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.TrabajadorReporteResponse;
import com.kynsoft.report.domain.dto.TrabajadorReporteDetailDto;
import com.kynsoft.report.domain.dto.TrabajadorReporteDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.ITrabajadorReporteService;
import com.kynsoft.report.infrastructure.entity.Reporte;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.entity.TrabajadorReporte;
import com.kynsoft.report.infrastructure.repository.command.TrabajadorReporteWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ReporteReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReporteReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
@AllArgsConstructor
public class TrabajadorReporteServiceImpl implements ITrabajadorReporteService {

    private final TrabajadorReporteWriteDataJPARepository repositoryCommand;
    private final TrabajadorReporteReadDataJPARepository repositoryQuery;
    private final ReporteReadDataJPARepository reporteRead;
    private final TrabajadorReadDataJPARepository trabajadorRead;
    private final AuditoriaTransaccionalService auditoria;

    @Override
    public void asignarTrabajadorAReporte(TrabajadorReporteDto object) {
        // Validar que el trabajador exista
        Trabajador tr = trabajadorRead.findById(object.getTrabajador())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("trabajadorId", "Trabajador not found."))));

        // Validar que el reporte exista
        Reporte r = reporteRead.findById(object.getReporte())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("reporteId", "Reporte not found."))));
        validarEscritura(r.getFincaId());
        if (r.getFincaId() != null && !r.getFincaId().equals(tr.getFincaId())) {
            throw new IllegalArgumentException("El trabajador debe pertenecer a la misma finca del reporte");
        }

        // Validar que no exista ya la relación
        repositoryQuery.findByTrabajadorIdAndReporteId(object.getTrabajador(), object.getReporte())
                .ifPresent(fp -> {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("trabajadorId", "Trabajador already assigned to this reporte.")));
                });

        // Crear la relación
        TrabajadorReporte trabajadorReporte = new TrabajadorReporte();
        trabajadorReporte.setId(object.getId());
        trabajadorReporte.setTrabajador(tr);
        trabajadorReporte.setReporte(r);
        trabajadorReporte.setNorma(ValidacionParteTrabajo.normaRequerida(object.getNorma()));
        trabajadorReporte.setHoras(ValidacionParteTrabajo.horas(object.getHoras()));

        TrabajadorReporte creado = repositoryCommand.save(trabajadorReporte);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE, "TRABAJADOR_REPORTE", creado.getId(),
                "Asignado trabajador a parte de trabajo", null, resumen(creado));
    }

    @Override
    public void actualizarTrabajadorEnReporte(TrabajadorReporteDto object) {
        TrabajadorReporte trabajadorReporte = repositoryQuery.findById(object.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "Relationship not found."))));
        validarEscritura(trabajadorReporte.getReporte().getFincaId());

        Map<String, Object> anterior = resumen(trabajadorReporte);
        trabajadorReporte.setHoras(ValidacionParteTrabajo.horas(object.getHoras()));
        trabajadorReporte.setNorma(ValidacionParteTrabajo.normaRequerida(object.getNorma()));

        TrabajadorReporte actualizado = repositoryCommand.save(trabajadorReporte);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE, "TRABAJADOR_REPORTE", actualizado.getId(),
                "Actualizada asignación de trabajador en parte", anterior, resumen(actualizado));
    }

    @Override
    public TrabajadorReporteDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(trabajadorReporte -> {
                    validarLectura(trabajadorReporte.getReporte().getFincaId());
                    return trabajadorReporte.toAggregate();
                })
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "Trabajador Reporte not found."))));
    }

    @Override
    public void remover(UUID id) {
        try {
            TrabajadorReporte trabajadorReporte = repositoryQuery.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "TrabajadorReporte not found."))));
            validarEscritura(trabajadorReporte.getReporte().getFincaId());
            repositoryCommand.deleteById(id);
            auditoria.registrarDespuesDeConfirmar(TipoAccion.DELETE, "TRABAJADOR_REPORTE", id,
                    "Eliminada asignación de trabajador en parte", resumen(trabajadorReporte), null);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "Element cannot be deleted as it has a related element.")));
        }
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TrabajadorReporte> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        org.springframework.data.jpa.domain.Specification<TrabajadorReporte> tenantSpec = (root, query, cb) -> {
            UUID fincaId = TenantContext.getEffectiveFincaId();
            return fincaId == null ? cb.conjunction()
                    : cb.equal(root.join("reporte").get("fincaId"), fincaId);
        };
        Page<TrabajadorReporte> data = repositoryQuery.findAll(
                org.springframework.data.jpa.domain.Specification.where(specifications).and(tenantSpec), pageable);
        return createPaginatedResponse(data);
    }

    @Override
    public List<TrabajadorReporteDetailDto> obtenerTrabajadoresConDetallesPorReporte(UUID reporteId) {
        validarLecturaReporte(reporteId);
        List<TrabajadorReporte> asignaciones = repositoryQuery.findByReporteId(reporteId);
        
        return asignaciones.stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
    }

    private PaginatedResponse createPaginatedResponse(Page<TrabajadorReporte> data) {
        List<TrabajadorReporteResponse> responses = data.getContent().stream()
                .map(fp -> new TrabajadorReporteResponse(
                fp.getId(),
                fp.getTrabajador().getId(),
                fp.getReporte().getId(),
                fp.getNorma(),
                fp.getHoras()
        ))
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

    private TrabajadorReporteDetailDto toDetailDto(TrabajadorReporte tr) {
        return TrabajadorReporteDetailDto.builder()
                .id(tr.getId())
                .trabajadorId(tr.getTrabajador().getId())
                .trabajadorNombre(tr.getTrabajador().getNombre())
                .trabajadorRuc(tr.getTrabajador().getRuc())
                .trabajadorCuenta(tr.getTrabajador().getCuenta())
                .trabajadorCargo(tr.getTrabajador().getCargo() != null ? tr.getTrabajador().getCargo().getName() : null)
                .reporteId(tr.getReporte().getId())
                .reporteCodigo(tr.getReporte().getCodigo())
                .reporteBloque(tr.getReporte().getBloque())
                .reporteCampo(tr.getReporte().getCampo())
                .reporteArea(tr.getReporte().getArea())
                .reporteNorma(tr.getReporte().getNorma())
                .reporteYear(tr.getReporte().getYear())
                .reporteMes(tr.getReporte().getMes())
                .norma(tr.getNorma())
                .horas(tr.getHoras())
                .build();
    }

    private Map<String, Object> resumen(TrabajadorReporte asignacion) {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("reporteId", asignacion.getReporte() == null ? null : asignacion.getReporte().getId());
        datos.put("trabajadorId", asignacion.getTrabajador() == null ? null : asignacion.getTrabajador().getId());
        datos.put("horas", asignacion.getHoras());
        datos.put("norma", asignacion.getNorma());
        return datos;
    }

    private void validarLecturaReporte(UUID reporteId) {
        Reporte reporte = reporteRead.findById(reporteId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("reporteId", "Reporte not found."))));
        validarLectura(reporte.getFincaId());
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
