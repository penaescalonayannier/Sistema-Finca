package com.kynsoft.report.applications.query.bloque.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IBloqueService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBloqueQueryHandler 
    implements IQueryHandler<GetSearchBloqueQuery, PaginatedResponse> {

    private final IBloqueService service;

    public GetSearchBloqueQueryHandler(IBloqueService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchBloqueQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}