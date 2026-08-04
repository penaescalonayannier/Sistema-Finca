package com.kynsoft.report.applications.query.report.estadoCuenta.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IEstadoCuentaService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchEstadoCuentaQueryHandler implements IQueryHandler<GetSearchEstadoCuentaQuery, PaginatedResponse> {

    private final IEstadoCuentaService service;

    public GetSearchEstadoCuentaQueryHandler(IEstadoCuentaService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchEstadoCuentaQuery query) {

        return this.service.search(query.getPageable(), query.getFilter());
    }
}
