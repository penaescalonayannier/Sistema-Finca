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
import com.kynsoft.report.domain.services.ITrabajadorDiaService;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.command.TrabajadorDiaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorDiaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrabajadorDiaServiceImpl implements ITrabajadorDiaService {

    private final TrabajadorDiaWriteDataJPARepository repositoryCommand;
    private final TrabajadorDiaReadDataJPARepository repositoryQuery;
    private final DiaTrabajoReadDataJPARepository diaTrabajoRepository;
    private final TrabajadorReadDataJPARepository trabajadorRepository;

    public TrabajadorDiaServiceImpl(
            TrabajadorDiaWriteDataJPARepository repositoryCommand,
            TrabajadorDiaReadDataJPARepository repositoryQuery,
            DiaTrabajoReadDataJPARepository diaTrabajoRepository,
            TrabajadorReadDataJPARepository trabajadorRepository) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.diaTrabajoRepository = diaTrabajoRepository;
        this.trabajadorRepository = trabajadorRepository;
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
        td.setHoras(object.getHoras());
        td.setNorma(object.getNorma());

        repositoryCommand.save(td);
    }

    @Override
    public void update(TrabajadorDiaDto object) {
        TrabajadorDia td = repositoryQuery.findById(object.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "TrabajadorDia no encontrado."))));

        td.setHoras(object.getHoras());
        td.setNorma(object.getNorma());

        repositoryCommand.save(td);
    }

    @Override
    public void delete(UUID id) {
        try {
            repositoryQuery.findById(id)
                    .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("id", "TrabajadorDia no encontrado."))));
            repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "No se puede eliminar el trabajador del día.")));
        }
    }

    @Override
    public TrabajadorDiaDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(TrabajadorDia::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "TrabajadorDia no encontrado."))));
    }

    @Override
    public List<TrabajadorDiaDto> findByDiaTrabajoId(UUID diaTrabajoId) {
        List<TrabajadorDia> trabajadores = repositoryQuery.findByDiaTrabajoIdWithDetails(diaTrabajoId);
        return trabajadores.stream()
                .map(TrabajadorDia::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TrabajadorDia> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<TrabajadorDia> data = repositoryQuery.findAll(specifications, pageable);
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