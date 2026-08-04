package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.LaborResponse;
import com.kynsoft.report.domain.dto.LaborDto;
import com.kynsoft.report.domain.services.ILaborService;
import com.kynsoft.report.infrastructure.entity.Labor;
import com.kynsoft.report.infrastructure.repository.command.LaborWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LaborReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LaborServiceImpl implements ILaborService {

    private final LaborWriteDataJPARepository repositoryCommand;
    private final LaborReadDataJPARepository repositoryQuery;

    public LaborServiceImpl(LaborWriteDataJPARepository repositoryCommand,
                            LaborReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(LaborDto object) {
        repositoryCommand.save(new Labor(object));
    }

    @Override
    public void update(LaborDto object) {
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
}