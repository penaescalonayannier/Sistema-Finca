package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.DiaTrabajoResponse;
import com.kynsoft.report.domain.dto.DiaTrabajoDto;
import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import com.kynsoft.report.domain.services.IDiaTrabajoService;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.Reporte;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.command.DiaTrabajoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ReporteReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DiaTrabajoServiceImpl implements IDiaTrabajoService {

    private final DiaTrabajoWriteDataJPARepository repositoryCommand;
    private final DiaTrabajoReadDataJPARepository repositoryQuery;
    private final ReporteReadDataJPARepository reporteReadDataJPARepository;

    public DiaTrabajoServiceImpl(DiaTrabajoWriteDataJPARepository repositoryCommand,
                                 DiaTrabajoReadDataJPARepository repositoryQuery,
                                 ReporteReadDataJPARepository reporteReadDataJPARepository) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.reporteReadDataJPARepository = reporteReadDataJPARepository;
    }

    @Override
    public void create(DiaTrabajoDto object) {
        // Validar que el reporte exista
        Reporte reporte = reporteReadDataJPARepository.findById(object.getReporteId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("reporteId", "Reporte not found."))));

        // Validar que no exista un día con la misma fecha para este reporte
        repositoryQuery.findByReporteIdAndFecha(object.getReporteId(), object.getFecha())
                .ifPresent(dt -> {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("fecha", "Ya existe un día con esta fecha para este reporte.")));
                });

        DiaTrabajo dt = new DiaTrabajo();
        dt.setId(object.getId());
        dt.setFecha(object.getFecha());
        dt.setReporte(reporte);
        
        repositoryCommand.save(dt);
    }

    @Override
    public void update(DiaTrabajoDto object) {
        // Buscar el día existente
        DiaTrabajo dt = repositoryQuery.findById(object.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Día de trabajo no encontrado."))));

        // Validar que no exista otro día con la misma fecha para este reporte
        if (object.getFecha() != null && !object.getFecha().equals(dt.getFecha())) {
            repositoryQuery.findByReporteIdAndFecha(dt.getReporte().getId(), object.getFecha())
                    .ifPresent(diaExistente -> {
                        if (!diaExistente.getId().equals(dt.getId())) {
                            throw new BusinessNotFoundException(new GlobalBusinessException(
                                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                                    new ErrorField("fecha", "Ya existe un día con esta fecha para este reporte.")));
                        }
                    });
            dt.setFecha(object.getFecha());
        }

        repositoryCommand.save(dt);
    }

    @Override
    public void delete(UUID id) {
        try {
            // Verificar que exista antes de eliminar
            repositoryQuery.findById(id)
                    .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("id", "Día de trabajo no encontrado."))));
            repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "No se puede eliminar el día porque tiene trabajadores asociados.")));
        }
    }

    @Override
    public DiaTrabajoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(DiaTrabajo::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Día de trabajo no encontrado."))));
    }

    @Override
    public List<DiaTrabajoDto> findByReporteId(UUID reporteId) {
        // CORREGIDO: Usar el método que trae los trabajadores
        List<DiaTrabajo> dias = repositoryQuery.findByReporteIdWithTrabajadores(reporteId);
        return dias.stream()
                .map(this::toDtoWithTrabajadores)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiaTrabajoDto> findByReporteIdWithTrabajadores(UUID reporteId) {
        List<DiaTrabajo> dias = repositoryQuery.findByReporteIdWithTrabajadores(reporteId);
        return dias.stream()
                .map(this::toDtoWithTrabajadores)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<DiaTrabajo> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<DiaTrabajo> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<DiaTrabajo> data) {
        List<DiaTrabajoResponse> responses = data.getContent().stream()
                .map(DiaTrabajo::toAggregate)
                .map(dto -> new DiaTrabajoResponse(
                    dto.getId(),
                    dto.getFecha(),
                    dto.getReporteId(),
                    null // Los trabajadores no se cargan en paginación
                ))
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    // Método auxiliar para convertir DiaTrabajo a DTO con trabajadores
    private DiaTrabajoDto toDtoWithTrabajadores(DiaTrabajo dia) {
        List<TrabajadorDiaDto> trabajadores = dia.getTrabajadores().stream()
                .map(td -> {
                    // Verificar que el trabajador no sea null
                    if (td.getTrabajador() == null) {
                        return TrabajadorDiaDto.builder()
                                .id(td.getId())
                                .diaTrabajoId(dia.getId())
                                .trabajadorId(null)
                                .trabajadorNombre("Trabajador no disponible")
                                .trabajadorRuc("-")
                                .horas(td.getHoras() != null ? td.getHoras() : "0")
                                .norma(td.getNorma() != null ? td.getNorma() : "-")
                                .build();
                    }
                    
                    return TrabajadorDiaDto.builder()
                            .id(td.getId())
                            .diaTrabajoId(dia.getId())
                            .trabajadorId(td.getTrabajador().getId())
                            .trabajadorNombre(td.getTrabajador().getNombre() != null ? 
                                    td.getTrabajador().getNombre() : "Sin nombre")
                            .trabajadorRuc(td.getTrabajador().getRuc() != null ? 
                                    td.getTrabajador().getRuc() : "-")
                            .horas(td.getHoras() != null ? td.getHoras() : "0")
                            .norma(td.getNorma() != null ? td.getNorma() : "-")
                            .build();
                })
                .collect(Collectors.toList());

        return DiaTrabajoDto.builder()
                .id(dia.getId())
                .fecha(dia.getFecha())
                .reporteId(dia.getReporte() != null ? dia.getReporte().getId() : null)
                .trabajadores(trabajadores)
                .build();
    }
}