package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.VariedadResponse;
import com.kynsoft.report.domain.dto.VariedadDto;
import com.kynsoft.report.domain.services.IVariedadService;
import com.kynsoft.report.infrastructure.entity.Variedad;
import com.kynsoft.report.infrastructure.repository.command.VariedadWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.VariedadReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class VariedadServiceImpl implements IVariedadService {

    private final VariedadWriteDataJPARepository repositoryCommand;
    private final VariedadReadDataJPARepository repositoryQuery;

    public VariedadServiceImpl(VariedadWriteDataJPARepository repositoryCommand,
                               VariedadReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(VariedadDto object) {
        repositoryCommand.save(new Variedad(object));
    }

    @Override
    public void update(VariedadDto object) {
        repositoryCommand.save(new Variedad(object));
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
    public VariedadDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Variedad::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Variedad not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Variedad> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Variedad> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Variedad> data) {
        List<VariedadResponse> responses = data.getContent().stream()
                .map(Variedad::toAggregate)
                .map(VariedadResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}