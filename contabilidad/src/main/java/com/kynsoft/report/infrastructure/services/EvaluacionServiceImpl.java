package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.EvaluacionResponse;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.services.IEvaluacionService;
import com.kynsoft.report.infrastructure.entity.Evaluacion;
import com.kynsoft.report.infrastructure.repository.command.EvaluacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.EvaluacionReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluacionServiceImpl implements IEvaluacionService {

    private final EvaluacionWriteDataJPARepository repositoryCommand;
    private final EvaluacionReadDataJPARepository repositoryQuery;

    public EvaluacionServiceImpl(EvaluacionWriteDataJPARepository repositoryCommand,
                                EvaluacionReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(EvaluacionDto object) {
        repositoryCommand.save(new Evaluacion(object));
    }

    @Override
    public void update(EvaluacionDto object) {
        repositoryCommand.save(new Evaluacion(object));
    }

    @Override
    public void delete(UUID id) {
        try {
            repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "Evaluación cannot be deleted as it has a related element.")));
        }
    }

    @Override
    public EvaluacionDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Evaluacion::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Evaluación not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Evaluacion> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Evaluacion> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Evaluacion> data) {
        List<EvaluacionResponse> responses = data.getContent().stream()
                .map(Evaluacion::toAggregate)
                .map(EvaluacionResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<EvaluacionDto> findByMesAndYear(String mes, Integer year) {
        return repositoryQuery.findByMesAndYear(mes, year).stream()
                .map(Evaluacion::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvaluacionDto> findByYearAndMeses(Integer year, List<String> meses) {
        return repositoryQuery.findByYearAndMesIn(year, meses).stream()
                .map(Evaluacion::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findDistinctMesesByYear(Integer year) {
        return repositoryQuery.findDistinctMesesByYear(year);
    }

    @Override
    public List<Integer> findDistinctYears() {
        return repositoryQuery.findDistinctYears();
    }

    @Override
    public Optional<EvaluacionDto> findByTrabajadorAndMesAndYear(UUID trabajadorId, String mes, Integer year) {
        return repositoryQuery.findByTrabajadorIdAndMesAndYear(trabajadorId, mes, year)
                .map(Evaluacion::toAggregate);
    }
}
