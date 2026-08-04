package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.Cuenta110EfectivoBancoResponse;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
import com.kynsoft.report.domain.services.ICuenta110EfectivoBancoService;
import com.kynsoft.report.infrastructure.entity.Cuenta110EfectivoBanco;
import com.kynsoft.report.infrastructure.repository.command.Cuenta110EfectivoBancoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.Cuenta110EfectivoBancoReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Cuenta110EfectivoBancoServiceImpl implements ICuenta110EfectivoBancoService {

    private final Cuenta110EfectivoBancoWriteDataJPARepository repositoryCommand;
    private final Cuenta110EfectivoBancoReadDataJPARepository repositoryQuery;

    public Cuenta110EfectivoBancoServiceImpl(Cuenta110EfectivoBancoWriteDataJPARepository repositoryCommand,
                                             Cuenta110EfectivoBancoReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(Cuenta110EfectivoBancoDto object) {
        repositoryCommand.save(new Cuenta110EfectivoBanco(object));
    }

    @Override
    public void update(Cuenta110EfectivoBancoDto object) {
        repositoryCommand.save(new Cuenta110EfectivoBanco(object));
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
    public Cuenta110EfectivoBancoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Cuenta110EfectivoBanco::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Business not found."))));
    }


    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Cuenta110EfectivoBanco> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Cuenta110EfectivoBanco> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Cuenta110EfectivoBanco> data) {
        List<Cuenta110EfectivoBancoResponse> businessResponses = data.getContent().stream()
                .map(Cuenta110EfectivoBanco::toAggregate)
                .map(Cuenta110EfectivoBancoResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(businessResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta110EfectivoBancoDto findUnique() {
        // 1. Obtener todos los registros. Usamos findAll() ya que la tabla es única.
        List<Cuenta110EfectivoBanco> all = repositoryQuery.findAll();

        if (all.isEmpty()) {
            // Si no hay ningún registro, lanzamos una excepción de que no existe.
            throw new RuntimeException("El registro único de Cuenta110 no existe y debe ser creado antes de actualizar.");
        }

        if (all.size() > 1) {
            // Si hay más de uno, lanzamos un error de negocio por integridad de datos violada.
            throw new RuntimeException("La tabla Cuenta110EfectivoBanco contiene más de un registro. Error crítico de integridad de datos.");
        }

        // 2. Si hay exactamente uno, lo mapeamos a DTO y lo devolvemos.
        return all.get(0).toAggregate();
    }
}