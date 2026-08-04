package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.CargoResponse;
import com.kynsoft.report.domain.dto.CargoDto;
import com.kynsoft.report.domain.services.ICargoService;
import com.kynsoft.report.infrastructure.entity.Cargo;
import com.kynsoft.report.infrastructure.repository.command.CargoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CargoReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CargoServiceImpl implements ICargoService {

    private final CargoWriteDataJPARepository repositoryCommand;
    private final CargoReadDataJPARepository repositoryQuery;

    public CargoServiceImpl(CargoWriteDataJPARepository repositoryCommand,
                           CargoReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(CargoDto object) {
        repositoryCommand.save(new Cargo(object));
    }

    @Override
    public void update(CargoDto object) {
        repositoryCommand.save(new Cargo(object));
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
    public CargoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Cargo::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cargo not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Cargo> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Cargo> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Cargo> data) {
        List<CargoResponse> responses = data.getContent().stream()
                .map(Cargo::toAggregate)
                .map(CargoResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
