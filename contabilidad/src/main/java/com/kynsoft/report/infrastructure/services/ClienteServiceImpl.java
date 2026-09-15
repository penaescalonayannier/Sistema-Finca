package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.ClienteResponse;
import com.kynsoft.report.domain.dto.ClienteDto;
import com.kynsoft.report.domain.services.IClienteService;
import com.kynsoft.report.infrastructure.entity.Cliente;
import com.kynsoft.report.infrastructure.repository.command.ClienteWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ClienteReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements IClienteService {

    private final ClienteWriteDataJPARepository repositoryCommand;
    private final ClienteReadDataJPARepository repositoryQuery;

    public ClienteServiceImpl(ClienteWriteDataJPARepository repositoryCommand,
                              ClienteReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(ClienteDto object) {
        repositoryCommand.save(new Cliente(object));
    }

    @Override
    public void update(ClienteDto object) {
        repositoryCommand.save(new Cliente(object));
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
    public ClienteDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Cliente::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cliente not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Cliente> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Cliente> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Cliente> data) {
        List<ClienteResponse> responses = data.getContent().stream()
                .map(Cliente::toAggregate)
                .map(ClienteResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
