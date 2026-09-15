package com.kynsoft.report.applications.query.salida.getall;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ISalidaService;
import org.springframework.stereotype.Component;

@Component
public class GetAllSalidaQueryHandler implements IQueryHandler<GetAllSalidaQuery, PaginatedResponse> {

    private final ISalidaService service;

    public GetAllSalidaQueryHandler(ISalidaService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetAllSalidaQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
