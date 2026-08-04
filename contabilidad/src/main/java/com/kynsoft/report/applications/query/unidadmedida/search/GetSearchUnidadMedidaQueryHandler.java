package com.kynsoft.report.applications.query.unidadmedida.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IUnidadMedidaService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchUnidadMedidaQueryHandler 
    implements IQueryHandler<GetSearchUnidadMedidaQuery, PaginatedResponse> {

    private final IUnidadMedidaService service;

    public GetSearchUnidadMedidaQueryHandler(IUnidadMedidaService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchUnidadMedidaQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}