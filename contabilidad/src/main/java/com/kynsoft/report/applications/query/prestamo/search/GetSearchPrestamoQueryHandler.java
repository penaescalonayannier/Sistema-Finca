package com.kynsoft.report.applications.query.prestamo.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IPrestamoService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPrestamoQueryHandler implements IQueryHandler<GetSearchPrestamoQuery, PaginatedResponse> {

    private final IPrestamoService service;

    public GetSearchPrestamoQueryHandler(IPrestamoService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchPrestamoQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
