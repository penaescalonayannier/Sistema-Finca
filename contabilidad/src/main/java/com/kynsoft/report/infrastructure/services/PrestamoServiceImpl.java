package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.PrestamoResponse;
import com.kynsoft.report.domain.dto.PrestamoDto;
import com.kynsoft.report.domain.services.IPrestamoService;
import com.kynsoft.report.infrastructure.entity.Prestamo;
import com.kynsoft.report.infrastructure.repository.command.PrestamoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.PrestamoReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PrestamoServiceImpl implements IPrestamoService {

    private final PrestamoWriteDataJPARepository repositoryCommand;
    private final PrestamoReadDataJPARepository repositoryQuery;

    public PrestamoServiceImpl(PrestamoWriteDataJPARepository repositoryCommand,
                               PrestamoReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(PrestamoDto object) {
        repositoryCommand.save(new Prestamo(object));
    }

    @Override
    public void update(PrestamoDto object) {
        repositoryCommand.save(new Prestamo(object));
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
    public PrestamoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Prestamo::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Prestamo not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Prestamo> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Prestamo> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Prestamo> data) {
        List<PrestamoResponse> responses = data.getContent().stream()
                .map(Prestamo::toAggregate)
                .map(PrestamoResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
