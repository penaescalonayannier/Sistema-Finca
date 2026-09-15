package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.InstrumentoTrabajoResponse;
import com.kynsoft.report.domain.dto.InstrumentoTrabajoDto;
import com.kynsoft.report.domain.services.IInstrumentoTrabajoService;
import com.kynsoft.report.infrastructure.entity.InstrumentoTrabajo;
import com.kynsoft.report.infrastructure.repository.command.InstrumentoTrabajoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.InstrumentoTrabajoReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InstrumentoTrabajoServiceImpl implements IInstrumentoTrabajoService {

    private final InstrumentoTrabajoWriteDataJPARepository repositoryCommand;
    private final InstrumentoTrabajoReadDataJPARepository repositoryQuery;

    public InstrumentoTrabajoServiceImpl(InstrumentoTrabajoWriteDataJPARepository repositoryCommand,
                                         InstrumentoTrabajoReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(InstrumentoTrabajoDto object) {
        repositoryCommand.save(new InstrumentoTrabajo(object));
    }

    @Override
    public void update(InstrumentoTrabajoDto object) {
        repositoryCommand.save(new InstrumentoTrabajo(object));
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
    public InstrumentoTrabajoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(InstrumentoTrabajo::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "InstrumentoTrabajo not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<InstrumentoTrabajo> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<InstrumentoTrabajo> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<InstrumentoTrabajo> data) {
        List<InstrumentoTrabajoResponse> responses = data.getContent().stream()
                .map(InstrumentoTrabajo::toAggregate)
                .map(InstrumentoTrabajoResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}