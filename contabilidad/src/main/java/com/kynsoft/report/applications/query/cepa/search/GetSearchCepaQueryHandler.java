package com.kynsoft.report.applications.query.cepa.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ICepaService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchCepaQueryHandler 
    implements IQueryHandler<GetSearchCepaQuery, PaginatedResponse> {

    private final ICepaService service;

    public GetSearchCepaQueryHandler(ICepaService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchCepaQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}