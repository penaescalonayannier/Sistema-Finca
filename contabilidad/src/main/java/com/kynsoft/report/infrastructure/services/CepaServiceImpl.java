package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.CepaResponse;
import com.kynsoft.report.domain.dto.CepaDto;
import com.kynsoft.report.domain.services.ICepaService;
import com.kynsoft.report.infrastructure.entity.Cepa;
import com.kynsoft.report.infrastructure.repository.command.CepaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CepaReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CepaServiceImpl implements ICepaService {

    private final CepaWriteDataJPARepository repositoryCommand;
    private final CepaReadDataJPARepository repositoryQuery;

    public CepaServiceImpl(CepaWriteDataJPARepository repositoryCommand,
                           CepaReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(CepaDto object) {
        repositoryCommand.save(new Cepa(object));
    }

    @Override
    public void update(CepaDto object) {
        repositoryCommand.save(new Cepa(object));
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
    public CepaDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Cepa::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cepa not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Cepa> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Cepa> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Cepa> data) {
        List<CepaResponse> responses = data.getContent().stream()
                .map(Cepa::toAggregate)
                .map(CepaResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}