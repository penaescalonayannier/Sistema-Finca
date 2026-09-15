package com.kynsoft.report.applications.query.cliente.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IClienteService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchClienteQueryHandler implements IQueryHandler<GetSearchClienteQuery, PaginatedResponse> {

    private final IClienteService service;

    public GetSearchClienteQueryHandler(IClienteService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchClienteQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
