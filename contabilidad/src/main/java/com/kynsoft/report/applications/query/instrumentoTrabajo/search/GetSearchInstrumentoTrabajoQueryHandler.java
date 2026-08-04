package com.kynsoft.report.applications.query.instrumentoTrabajo.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IInstrumentoTrabajoService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchInstrumentoTrabajoQueryHandler 
    implements IQueryHandler<GetSearchInstrumentoTrabajoQuery, PaginatedResponse> {

    private final IInstrumentoTrabajoService service;

    public GetSearchInstrumentoTrabajoQueryHandler(IInstrumentoTrabajoService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchInstrumentoTrabajoQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}