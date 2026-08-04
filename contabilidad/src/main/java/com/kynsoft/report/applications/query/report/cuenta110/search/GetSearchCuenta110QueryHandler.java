package com.kynsoft.report.applications.query.report.cuenta110.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ICuenta110EfectivoBancoService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchCuenta110QueryHandler implements IQueryHandler<GetSearchCuenta110Query, PaginatedResponse> {

    private final ICuenta110EfectivoBancoService service;

    public GetSearchCuenta110QueryHandler(ICuenta110EfectivoBancoService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchCuenta110Query query) {

        return this.service.search(query.getPageable(), query.getFilter());
    }
}
