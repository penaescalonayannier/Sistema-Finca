package com.kynsoft.report.applications.query.campos.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ICamposService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchCamposQueryHandler 
    implements IQueryHandler<GetSearchCamposQuery, PaginatedResponse> {

    private final ICamposService service;

    public GetSearchCamposQueryHandler(ICamposService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchCamposQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}