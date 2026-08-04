package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.CamposResponse;
import com.kynsoft.report.domain.dto.CampoDto;
import com.kynsoft.report.domain.services.ICamposService;
import com.kynsoft.report.infrastructure.entity.Campo;
import com.kynsoft.report.infrastructure.repository.command.CamposWriteDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.kynsoft.report.infrastructure.repository.query.CampoReadDataJPARepository;

@Service
public class CamposServiceImpl implements ICamposService {

    private final CamposWriteDataJPARepository repositoryCommand;
    private final CampoReadDataJPARepository repositoryQuery;

    public CamposServiceImpl(CamposWriteDataJPARepository repositoryCommand,
                             CampoReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(CampoDto object) {
        repositoryCommand.save(new Campo(object));
    }

    @Override
    public void update(CampoDto object) {
        repositoryCommand.save(new Campo(object));
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
    public CampoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Campo::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cliente not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Campo> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Campo> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Campo> data) {
        List<CamposResponse> responses = data.getContent().stream()
                .map(Campo::toAggregate)
                .map(CamposResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
