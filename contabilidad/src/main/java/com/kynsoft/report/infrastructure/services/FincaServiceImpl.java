package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.FincaResponse;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.services.IFincaService;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.repository.command.FincaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FincaServiceImpl implements IFincaService {

    private final FincaWriteDataJPARepository repositoryCommand;
    private final FincaReadDataJPARepository repositoryQuery;

    public FincaServiceImpl(FincaWriteDataJPARepository repositoryCommand,
                            FincaReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(FincaDto object) {
        // Validar que el código no exista
        if (object.getCode() != null) {
            repositoryQuery.findByCode(object.getCode())
                .ifPresent(finca -> {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("code", "Finca with code " + object.getCode() + " already exists.")));
                });
        }
        repositoryCommand.save(new Finca(object));
    }

    @Override
    public void update(FincaDto object) {
        // Verificar que la finca exista
        repositoryQuery.findById(object.getId())
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Finca not found."))));
        
        // Verificar que el código no esté duplicado (excluyendo la misma finca)
        if (object.getCode() != null) {
            repositoryQuery.findByCode(object.getCode())
                .ifPresent(finca -> {
                    if (!finca.getId().equals(object.getId())) {
                        throw new BusinessNotFoundException(new GlobalBusinessException(
                                DomainErrorMessage.BUSINESS_NOT_FOUND,
                                new ErrorField("code", "Finca with code " + object.getCode() + " already exists.")));
                    }
                });
        }
        
        repositoryCommand.save(new Finca(object));
    }

    @Override
    public void delete(UUID id) {
        try {
            // Verificar que la finca exista antes de eliminar
            repositoryQuery.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Finca not found."))));
            repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "Element cannot be deleted as it has a related element.")));
        }
    }

    @Override
    public FincaDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Finca::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Finca not found."))));
    }

    @Override
    public FincaDto findByCode(String code) {
        return repositoryQuery.findByCode(code)
                .map(Finca::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("code", "Finca with code " + code + " not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Finca> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Finca> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Finca> data) {
        List<FincaResponse> responses = data.getContent().stream()
                .map(Finca::toAggregate)
                .map(FincaResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}