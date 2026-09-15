package com.kynsoft.report.applications.query.labor.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ILaborService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchLaborQueryHandler 
    implements IQueryHandler<GetSearchLaborQuery, PaginatedResponse> {

    private final ILaborService service;

    public GetSearchLaborQueryHandler(ILaborService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchLaborQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}