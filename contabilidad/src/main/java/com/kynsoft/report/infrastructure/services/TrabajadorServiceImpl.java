package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.TrabajadorResponse;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.TrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TrabajadorServiceImpl implements ITrabajadorService {

    private final TrabajadorWriteDataJPARepository repositoryCommand;
    private final TrabajadorReadDataJPARepository repositoryQuery;

    public TrabajadorServiceImpl(TrabajadorWriteDataJPARepository repositoryCommand,
            TrabajadorReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(TrabajadorDto object) {
        repositoryCommand.save(new Trabajador(object));
    }

    @Override
    public void update(TrabajadorDto object) {
        repositoryCommand.save(new Trabajador(object));
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
    public TrabajadorDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Trabajador::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "Trabajador not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Trabajador> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Trabajador> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Trabajador> data) {
        List<TrabajadorResponse> responses = data.getContent().stream()
                .map(Trabajador::toAggregate)
                .map(TrabajadorResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<TrabajadorDto> findAll(List<UUID> ids) {
        return this.repositoryQuery.findAllById(ids).stream()
                .map(Trabajador::toAggregate)
                .toList(); // En Java 16+ usa .toList(), en versiones anteriores .collect(Collectors.toList())
    }
}
