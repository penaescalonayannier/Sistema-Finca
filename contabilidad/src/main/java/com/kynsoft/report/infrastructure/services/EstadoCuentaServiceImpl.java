package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.EstadoCuentaResponse;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import com.kynsoft.report.domain.services.IEstadoCuentaService;
import com.kynsoft.report.infrastructure.entity.EstadoCuenta;
import com.kynsoft.report.infrastructure.repository.command.EstadoCuentaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.EstadoCuentaReadDataJPARepository;
import com.kynsoft.report.infrastructure.util.specification.EstadoCuentaDateSpecification;
import com.kynsoft.report.infrastructure.util.specification.EstadoCuentaSpecification;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;

@Service
public class EstadoCuentaServiceImpl implements IEstadoCuentaService {

    private final EstadoCuentaWriteDataJPARepository repositoryCommand;
    private final EstadoCuentaReadDataJPARepository repositoryQuery;

    public EstadoCuentaServiceImpl(EstadoCuentaWriteDataJPARepository repositoryCommand,
                                   EstadoCuentaReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(EstadoCuentaDto object) {
        repositoryCommand.save(new EstadoCuenta(object));
    }

    @Override
    public void update(EstadoCuentaDto object) {
        repositoryCommand.save(new EstadoCuenta(object));
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
    public void deleteIds(List<UUID> ids) {
        repositoryCommand.deleteAllByIdInBatch(ids);
    }

    @Override
    public EstadoCuentaDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(EstadoCuenta::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Business not found."))));
    }


    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<EstadoCuenta> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<EstadoCuenta> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<EstadoCuenta> data) {
        List<EstadoCuentaResponse> businessResponses = data.getContent().stream()
                .map(EstadoCuenta::toAggregate)
                .map(EstadoCuentaResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(businessResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<EstadoCuentaDto> findAll(String query, String fechaInicio, String fechaFin, String filterTipo) {
        // 1. Crear la Specification con los filtros
        Specification<EstadoCuenta> specification =  EstadoCuentaSpecification.getFilter(query, fechaInicio, fechaFin, filterTipo);

        // 2. Llamar al método findAll(Specification) que ahora está disponible
        //    gracias a que el repositorio extiende JpaSpecificationExecutor.
        List<EstadoCuenta> entities = repositoryQuery.findAll(specification);

        // 3. Mapear las entidades a DTOs
        return entities.stream()
                .map(x -> x.toAggregate())
                .collect(Collectors.toList());
    }

    @Override
    public List<EstadoCuentaDto> findAllByDate(LocalDate fechaInicio, LocalDate fechaFin) {
        
        // 1. Crear la Specification usando la utilidad de solo fechas
        Specification<EstadoCuenta> specification = 
            EstadoCuentaDateSpecification.filterByDateRange(fechaInicio, fechaFin);

        // 2. Llamar al método findAll(Specification) del repositorio (no paginado)
        List<EstadoCuenta> entities = repositoryQuery.findAll(specification);

        // 3. Mapear las entidades a DTOs
        return entities.stream()
                // Se asume que 'toAggregate()' mapea la entidad a DTO/Aggregate, 
                // si no es así, use su método de mapeo (p. ej., 'this::toDto')
                .map(x -> x.toAggregate()) 
                .collect(Collectors.toList());
    }
}