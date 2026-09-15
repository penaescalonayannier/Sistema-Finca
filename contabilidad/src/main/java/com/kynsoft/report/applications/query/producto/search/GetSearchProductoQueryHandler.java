package com.kynsoft.report.applications.query.producto.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IProductoService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchProductoQueryHandler 
    implements IQueryHandler<GetSearchProductoQuery, PaginatedResponse> {

    private final IProductoService service;

    public GetSearchProductoQueryHandler(IProductoService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchProductoQuery query) {
        return service.search(query.getPageable(), query.getFilter(), query.getQuery());
    }
}