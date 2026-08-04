package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.GrupoResponse;
import com.kynsoft.report.domain.dto.GrupoDto;
import com.kynsoft.report.domain.services.IGrupoService;
import com.kynsoft.report.infrastructure.entity.Grupo;
import com.kynsoft.report.infrastructure.repository.command.GrupoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.GrupoReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GrupoServiceImpl implements IGrupoService {

    private final GrupoWriteDataJPARepository repositoryCommand;
    private final GrupoReadDataJPARepository repositoryQuery;

    public GrupoServiceImpl(GrupoWriteDataJPARepository repositoryCommand,
                           GrupoReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(GrupoDto object) {
        repositoryCommand.save(new Grupo(object));
    }

    @Override
    public void update(GrupoDto object) {
        repositoryCommand.save(new Grupo(object));
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
    public GrupoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Grupo::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Grupo not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Grupo> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Grupo> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Grupo> data) {
        List<GrupoResponse> responses = data.getContent().stream()
                .map(Grupo::toAggregate)
                .map(GrupoResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
