package com.kynsoft.report.applications.query.finca.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IFincaService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchFincaQueryHandler 
    implements IQueryHandler<GetSearchFincaQuery, PaginatedResponse> {

    private final IFincaService service;

    public GetSearchFincaQueryHandler(IFincaService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchFincaQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}