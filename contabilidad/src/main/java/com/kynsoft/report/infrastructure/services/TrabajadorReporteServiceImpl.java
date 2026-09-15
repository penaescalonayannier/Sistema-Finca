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
import com.kynsoft.report.domain.services.ITrabajadorReporteService;
import com.kynsoft.report.infrastructure.entity.Reporte;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.entity.TrabajadorReporte;
import com.kynsoft.report.infrastructure.repository.command.TrabajadorReporteWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ReporteReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReporteReadDataJPARepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

        // Validar que no exista ya la relación
        repositoryQuery.findByTrabajadorIdAndReporteId(object.getTrabajador(), object.getReporte())
                .ifPresent(fp -> {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("trabajadorId", "Trabajador already assigned to this reporte.")));
                });

        // Validar que las horas no excedan 8
        validarHoras(object.getHoras());

        // Crear la relación
        TrabajadorReporte trabajadorReporte = new TrabajadorReporte();
        trabajadorReporte.setId(object.getId());
        trabajadorReporte.setTrabajador(tr);
        trabajadorReporte.setReporte(r);
        trabajadorReporte.setNorma(object.getNorma());
        trabajadorReporte.setHoras(object.getHoras());

        repositoryCommand.save(trabajadorReporte);
    }

    @Override
    public void actualizarTrabajadorEnReporte(TrabajadorReporteDto object) {
        TrabajadorReporte trabajadorReporte = repositoryQuery.findById(object.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "Relationship not found."))));

        // Validar que las horas no excedan 8
        validarHoras(object.getHoras());

        trabajadorReporte.setHoras(object.getHoras());
        trabajadorReporte.setNorma(object.getNorma());

        repositoryCommand.save(trabajadorReporte);
    }

    @Override
    public TrabajadorReporteDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(TrabajadorReporte::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "Trabajador Reporte not found."))));
    }

    @Override
    public void remover(UUID id) {
        try {
            repositoryQuery.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "TrabajadorReporte not found."))));
            repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "Element cannot be deleted as it has a related element.")));
        }
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TrabajadorReporte> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<TrabajadorReporte> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    @Override
    public List<TrabajadorReporteDetailDto> obtenerTrabajadoresConDetallesPorReporte(UUID reporteId) {
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

    private void validarHoras(String horasString) {
        double horas;
        try {
            horas = Double.parseDouble(horasString);
        } catch (NumberFormatException e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("horas", "Las horas deben ser un valor numérico válido.")));
        }

        if (horas > 8.0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("horas", "Las horas no pueden exceder 8 horas por día. Horas ingresadas: " + horas)));
        }
    }
}