package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.BloqueResponse;
import com.kynsoft.report.domain.dto.BloqueDto;
import com.kynsoft.report.domain.services.IBloqueService;
import com.kynsoft.report.infrastructure.entity.Bloque;
import com.kynsoft.report.infrastructure.repository.command.BloqueWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.BloqueReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BloqueServiceImpl implements IBloqueService {

    private final BloqueWriteDataJPARepository repositoryCommand;
    private final BloqueReadDataJPARepository repositoryQuery;

    public BloqueServiceImpl(BloqueWriteDataJPARepository repositoryCommand,
                             BloqueReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(BloqueDto object) {
        repositoryCommand.save(new Bloque(object));
    }

    @Override
    public void update(BloqueDto object) {
        repositoryCommand.save(new Bloque(object));
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
    public BloqueDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Bloque::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Bloque not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Bloque> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Bloque> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Bloque> data) {
        List<BloqueResponse> responses = data.getContent().stream()
                .map(Bloque::toAggregate)
                .map(BloqueResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}