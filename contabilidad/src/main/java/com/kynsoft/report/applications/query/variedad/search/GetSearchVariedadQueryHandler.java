package com.kynsoft.report.applications.query.variedad.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IVariedadService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchVariedadQueryHandler 
    implements IQueryHandler<GetSearchVariedadQuery, PaginatedResponse> {

    private final IVariedadService service;

    public GetSearchVariedadQueryHandler(IVariedadService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchVariedadQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}