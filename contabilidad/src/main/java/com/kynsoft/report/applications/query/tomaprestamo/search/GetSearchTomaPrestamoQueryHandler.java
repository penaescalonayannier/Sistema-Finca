package com.kynsoft.report.applications.query.tomaprestamo.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ITomaPrestamoService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchTomaPrestamoQueryHandler 
    implements IQueryHandler<GetSearchTomaPrestamoQuery, PaginatedResponse> {

    private final ITomaPrestamoService service;

    public GetSearchTomaPrestamoQueryHandler(ITomaPrestamoService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchTomaPrestamoQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}