package com.kynsoft.report.applications.query.deudaTrabajador.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IDeudaTrabajadorService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchDeudaTrabajadorQueryHandler implements IQueryHandler<GetSearchDeudaTrabajadorQuery, PaginatedResponse> {

    private final IDeudaTrabajadorService service;

    public GetSearchDeudaTrabajadorQueryHandler(IDeudaTrabajadorService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchDeudaTrabajadorQuery query) {
        return service.search(query.getPageable(), query.getFilterCriteria());
    }
}
