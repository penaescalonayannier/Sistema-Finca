package com.kynsoft.report.applications.query.produccionterminada.getall;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.ProduccionTerminadaResponse;
import com.kynsoft.report.infrastructure.entity.ProduccionTerminada;
import com.kynsoft.report.infrastructure.repository.query.ProduccionTerminadaReadDataJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetAllProduccionTerminadaQueryHandler
        implements IQueryHandler<GetAllProduccionTerminadaQuery, PaginatedResponse> {

    private final ProduccionTerminadaReadDataJPARepository repositoryQuery;

    @Override
    public PaginatedResponse handle(GetAllProduccionTerminadaQuery query) {
        GenericSpecificationsBuilder<ProduccionTerminada> specifications
                = new GenericSpecificationsBuilder<>(query.getFilter());

        Page<ProduccionTerminada> data = repositoryQuery.findAll(specifications, query.getPageable());

        List<ProduccionTerminadaResponse> responses = data.getContent().stream()
                .map(pt -> new ProduccionTerminadaResponse(pt.toAggregate()))
                .collect(Collectors.toList());

        return new PaginatedResponse(
                responses,
                data.getTotalPages(),
                data.getNumberOfElements(),
                data.getTotalElements(),
                data.getSize(),
                data.getNumber()
        );
    }
}
