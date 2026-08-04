package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.HombreActividadAgricolaImporteResponse;
import com.kynsoft.report.domain.dto.HombreActividadAgricolaImporteDto;
import com.kynsoft.report.domain.services.IHombreActividadAgricolaImporteService;
import com.kynsoft.report.infrastructure.entity.HombreActividadAgricolaImporte;
import com.kynsoft.report.infrastructure.repository.command.HombreActividadAgricolaImporteWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.HombreActividadAgricolaImporteReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HombreActividadAgricolaImporteServiceImpl implements IHombreActividadAgricolaImporteService {

    private final HombreActividadAgricolaImporteWriteDataJPARepository repositoryCommand;
    private final HombreActividadAgricolaImporteReadDataJPARepository repositoryQuery;

    public HombreActividadAgricolaImporteServiceImpl(HombreActividadAgricolaImporteWriteDataJPARepository repositoryCommand,
                                                     HombreActividadAgricolaImporteReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(HombreActividadAgricolaImporteDto object) {
        repositoryCommand.save(new HombreActividadAgricolaImporte(object));
    }

    @Override
    public void update(HombreActividadAgricolaImporteDto object) {
        repositoryCommand.save(new HombreActividadAgricolaImporte(object));
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
    public HombreActividadAgricolaImporteDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(HombreActividadAgricolaImporte::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "HombreActividadAgricolaImporte not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<HombreActividadAgricolaImporte> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<HombreActividadAgricolaImporte> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<HombreActividadAgricolaImporte> data) {
        List<HombreActividadAgricolaImporteResponse> responses = data.getContent().stream()
                .map(HombreActividadAgricolaImporte::toAggregate)
                .map(HombreActividadAgricolaImporteResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}