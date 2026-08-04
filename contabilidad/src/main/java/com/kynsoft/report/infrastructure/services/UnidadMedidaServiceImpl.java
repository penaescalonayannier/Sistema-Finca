package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.UnidadMedidaResponse;
import com.kynsoft.report.domain.dto.UnidadMedidaDto;
import com.kynsoft.report.domain.services.IUnidadMedidaService;
import com.kynsoft.report.infrastructure.entity.UnidadMedida;
import com.kynsoft.report.infrastructure.repository.command.UnidadMedidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.UnidadMedidaReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UnidadMedidaServiceImpl implements IUnidadMedidaService {

    private final UnidadMedidaWriteDataJPARepository repositoryCommand;
    private final UnidadMedidaReadDataJPARepository repositoryQuery;

    public UnidadMedidaServiceImpl(UnidadMedidaWriteDataJPARepository repositoryCommand,
                                   UnidadMedidaReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(UnidadMedidaDto object) {
        repositoryCommand.save(new UnidadMedida(object));
    }

    @Override
    public void update(UnidadMedidaDto object) {
        repositoryCommand.save(new UnidadMedida(object));
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
    public UnidadMedidaDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(UnidadMedida::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "UnidadMedida not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<UnidadMedida> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<UnidadMedida> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<UnidadMedida> data) {
        List<UnidadMedidaResponse> responses = data.getContent().stream()
                .map(UnidadMedida::toAggregate)
                .map(UnidadMedidaResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}