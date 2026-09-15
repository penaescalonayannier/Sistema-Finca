package com.kynsoft.report.applications.query.almacen.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IAlmacenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetSearchAlmacenQueryHandler implements IQueryHandler<GetSearchAlmacenQuery, PaginatedResponse> {

    private final IAlmacenService serviceImpl;

    @Override
    public PaginatedResponse handle(GetSearchAlmacenQuery query) {
        return serviceImpl.search(query.getPageable(), query.getFilterCriteria());
    }
}
