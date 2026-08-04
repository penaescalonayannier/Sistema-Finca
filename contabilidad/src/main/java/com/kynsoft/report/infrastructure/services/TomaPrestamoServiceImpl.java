package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.TomaPrestamoResponse;
import com.kynsoft.report.domain.dto.TomaPrestamoDto;
import com.kynsoft.report.domain.services.ITomaPrestamoService;
import com.kynsoft.report.infrastructure.entity.TomaPrestamo;
import com.kynsoft.report.infrastructure.repository.command.TomaPrestamoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TomaPrestamoReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TomaPrestamoServiceImpl implements ITomaPrestamoService {

    private final TomaPrestamoWriteDataJPARepository repositoryCommand;
    private final TomaPrestamoReadDataJPARepository repositoryQuery;

    public TomaPrestamoServiceImpl(TomaPrestamoWriteDataJPARepository repositoryCommand,
                                TomaPrestamoReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(TomaPrestamoDto object) {
        repositoryCommand.save(new TomaPrestamo(object));
    }

    @Override
    public void update(TomaPrestamoDto object) {
        repositoryCommand.save(new TomaPrestamo(object));
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
    public TomaPrestamoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(TomaPrestamo::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Prestamo not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TomaPrestamo> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<TomaPrestamo> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<TomaPrestamo> data) {
        List<TomaPrestamoResponse> responses = data.getContent().stream()
                .map(TomaPrestamo::toAggregate)
                .map(TomaPrestamoResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
