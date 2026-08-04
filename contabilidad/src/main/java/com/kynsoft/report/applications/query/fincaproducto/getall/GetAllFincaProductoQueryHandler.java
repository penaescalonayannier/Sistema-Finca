package com.kynsoft.report.applications.query.fincaproducto.getall;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.FincaProductoResponse;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetAllFincaProductoQueryHandler
        implements IQueryHandler<GetAllFincaProductoQuery, PaginatedResponse> {

    private final FincaProductoReadDataJPARepository repositoryQuery;

    @Override
    public PaginatedResponse handle(GetAllFincaProductoQuery query) {
        GenericSpecificationsBuilder<FincaProducto> specifications
                = new GenericSpecificationsBuilder<>(query.getFilter());

        Page<FincaProducto> data = repositoryQuery.findAll(specifications, query.getPageable());

        List<FincaProductoResponse> responses = data.getContent().stream()
                .map(fp -> new FincaProductoResponse(
                fp.getId(),
                fp.getFinca().getId(),
                fp.getFinca().getCode(),
                fp.getFinca().getName(),
                fp.getProducto().getId(),
                fp.getProducto().getCode(),
                fp.getProducto().getName(),
                fp.getProducto().getPrice(),
                fp.getStock()
        ))
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
